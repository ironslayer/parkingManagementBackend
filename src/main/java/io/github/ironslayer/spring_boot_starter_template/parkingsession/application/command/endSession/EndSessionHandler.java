package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoActiveSessionException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.ParkingSessionNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Handler para finalizar una sesión de parqueo.
 * Calcula el tiempo y monto a pagar, libera el espacio.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EndSessionHandler implements RequestHandler<EndSessionRequest, EndSessionResponse> {
    
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final RateConfigRepository rateConfigRepository;
    
    @Override
    public EndSessionResponse handle(EndSessionRequest request) {
        log.info("Ending parking session for vehicle: {} or session: {}", 
                request.licensePlate(), request.sessionId());
        
        // Validar entrada
        validateRequest(request);
        
        // Obtener sesión activa
        ParkingSession session = getActiveSession(request);
        
        // Validar operador
        User operator = userRepository.findById(request.operatorId())
                .orElseThrow(() -> new UserNotFoundException("User with ID '" + request.operatorId() + "' not found"));
        
        // Registrar salida
        session.markExit(operator.getId());
        
        // Calcular tiempo y monto
        double hoursParked = session.calculateParkedHours();
        BigDecimal totalAmount = calculateTotalAmount(session, hoursParked);
        
        // Obtener información adicional
        Vehicle vehicle = vehicleRepository.findById(session.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(session.getVehicleId()));
        
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(session.getParkingSpaceId())
                .orElseThrow(() -> new RuntimeException("Parking space not found"));
        
        VehicleType vehicleType = vehicleTypeRepository.findById(vehicle.getVehicleTypeId())
                .orElseThrow(() -> new VehicleTypeNotFoundException(vehicle.getVehicleTypeId()));
        
        // Liberar espacio
        parkingSpace.free();
        parkingSpaceRepository.save(parkingSpace);
        
        // Guardar sesión actualizada
        ParkingSession savedSession = parkingSessionRepository.save(session);
        
        log.info("Parking session ended with ID: {} for vehicle: {}, Total amount: {}", 
                savedSession.getId(), vehicle.getLicensePlate(), totalAmount);
        
        return new EndSessionResponse(
                savedSession.getId(),
                vehicle.getLicensePlate(),
                vehicleType.getName(),
                parkingSpace.getSpaceNumber(),
                savedSession.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                savedSession.getExitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                BigDecimal.valueOf(hoursParked).setScale(2, RoundingMode.HALF_UP),
                totalAmount,
                operator.getFirstname() + " " + operator.getLastname()
        );
    }
    
    @Override
    public Class<EndSessionRequest> getRequestType() {
        return EndSessionRequest.class;
    }
    
    private void validateRequest(EndSessionRequest request) {
        if (request.operatorId() == null) {
            throw new BadRequestException("Operator ID is required");
        }
        
        int paramCount = 0;
        if (request.licensePlate() != null) paramCount++;
        if (request.sessionId() != null) paramCount++;
        if (request.ticketCode() != null) paramCount++;
        
        if (paramCount != 1) {
            throw new BadRequestException("Provide exactly one of: license plate, session ID, or ticket code");
        }
    }
    
    private ParkingSession getActiveSession(EndSessionRequest request) {
        if (request.licensePlate() != null) {
            // Buscar por placa
            Vehicle vehicle = vehicleRepository.findByLicensePlate(request.licensePlate().toUpperCase().trim())
                    .orElseThrow(() -> new VehicleNotFoundException(request.licensePlate()));
            
            return parkingSessionRepository.findActiveSessionByVehicleId(vehicle.getId())
                    .orElseThrow(() -> new NoActiveSessionException(request.licensePlate()));
                    
        } else if (request.sessionId() != null) {
            // Buscar por ID de sesión
            ParkingSession session = parkingSessionRepository.findById(request.sessionId())
                    .orElseThrow(() -> new ParkingSessionNotFoundException(request.sessionId()));
            
            if (!session.canRegisterExit()) {
                throw new NoActiveSessionException();
            }
            
            return session;
            
                } else if (request.ticketCode() != null) {
            // Buscar por código de ticket
            return parkingSessionRepository.findByTicketCode(request.ticketCode())
                    .filter(ParkingSession::canRegisterExit)
                    .orElseThrow(() -> new ParkingSessionNotFoundException(request.ticketCode()));
        }
        
        throw new BadRequestException("Invalid session identification");
    }
    
    private BigDecimal calculateTotalAmount(ParkingSession session, double hoursParked) {
        // Obtener vehículo para conocer su tipo
        Vehicle vehicle = vehicleRepository.findById(session.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(session.getVehicleId()));
        
        // Obtener configuración de tarifa activa
        Optional<RateConfig> rateConfig = rateConfigRepository.findActiveByVehicleTypeId(vehicle.getVehicleTypeId());
        
        if (rateConfig.isEmpty()) {
            throw new RuntimeException("No active rate configuration found for vehicle type");
        }
        
        RateConfig config = rateConfig.get();
        
        // Aplicar tiempo mínimo de cobro
        double billableHours = Math.max(hoursParked, config.getMinimumChargeHours());
        
        // Calcular monto base
        BigDecimal baseAmount = config.getRatePerHour().multiply(BigDecimal.valueOf(billableHours));
        
        // Aplicar límite máximo diario si existe
        BigDecimal totalAmount = baseAmount;
        if (config.getMaximumDailyRate() != null && baseAmount.compareTo(config.getMaximumDailyRate()) > 0) {
            totalAmount = config.getMaximumDailyRate();
        }
        
        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
