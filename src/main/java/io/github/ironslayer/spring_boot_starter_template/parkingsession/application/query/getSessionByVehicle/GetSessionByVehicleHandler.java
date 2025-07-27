package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getSessionByVehicle;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoActiveSessionException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Handler para obtener la sesión activa de un vehículo específico.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetSessionByVehicleHandler implements RequestHandler<GetSessionByVehicleRequest, GetSessionByVehicleResponse> {
    
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    private final RateConfigRepository rateConfigRepository;
    
    @Override
    public GetSessionByVehicleResponse handle(GetSessionByVehicleRequest request) {
        log.info("Retrieving active session for vehicle: {}", request.licensePlate());
        
        // Validar entrada
        if (request.licensePlate() == null || request.licensePlate().trim().isEmpty()) {
            throw new BadRequestException("License plate is required");
        }
        
        // Buscar vehículo
        Vehicle vehicle = vehicleRepository.findByLicensePlate(request.licensePlate().toUpperCase().trim())
                .orElseThrow(() -> new VehicleNotFoundException(request.licensePlate()));
        
        // Buscar sesión activa
        ParkingSession session = parkingSessionRepository.findActiveSessionByVehicleId(vehicle.getId())
                .orElseThrow(() -> new NoActiveSessionException(request.licensePlate()));
        
        // Obtener información adicional
        VehicleType vehicleType = vehicleTypeRepository.findById(vehicle.getVehicleTypeId())
                .orElse(null);
        
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(session.getParkingSpaceId())
                .orElse(null);
        
        User operator = userRepository.findById(session.getOperatorEntryId())
                .orElse(null);
        
        // Calcular tiempo y monto estimado
        double hoursParked = session.calculateParkedHours();
        BigDecimal estimatedAmount = calculateEstimatedAmount(vehicle, hoursParked);
        
        log.info("Found active session {} for vehicle: {}", session.getId(), request.licensePlate());
        
        return new GetSessionByVehicleResponse(
                session.getId(),
                vehicle.getLicensePlate(),
                vehicleType != null ? vehicleType.getName() : "Unknown",
                parkingSpace != null ? parkingSpace.getSpaceNumber() : "Unknown",
                session.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                BigDecimal.valueOf(hoursParked).setScale(2, RoundingMode.HALF_UP),
                estimatedAmount,
                operator != null ? operator.getFirstname() + " " + operator.getLastname() : "Unknown",
                session.generateTicketCode()
        );
    }
    
    @Override
    public Class<GetSessionByVehicleRequest> getRequestType() {
        return GetSessionByVehicleRequest.class;
    }
    
    private BigDecimal calculateEstimatedAmount(Vehicle vehicle, double hoursParked) {
        try {
            Optional<RateConfig> rateConfig = rateConfigRepository.findActiveByVehicleTypeId(vehicle.getVehicleTypeId());
            
            if (rateConfig.isEmpty()) {
                return BigDecimal.ZERO;
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
            
        } catch (Exception e) {
            log.warn("Error calculating estimated amount for vehicle {}: {}", vehicle.getLicensePlate(), e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
