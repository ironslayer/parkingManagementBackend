package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoAvailableSpaceException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.VehicleAlreadyParkedException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handler para iniciar una nueva sesión de parqueo.
 * Valida que el vehículo no tenga sesión activa y que haya espacios disponibles.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StartSessionHandler implements RequestHandler<StartSessionRequest, StartSessionResponse> {
    
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    
    @Override
    public StartSessionResponse handle(StartSessionRequest request) {
        log.info("=== STARTING PARKING SESSION ===");
        log.info("Starting parking session for vehicle: {}", request.licensePlate());
        log.info("Operator ID: {}", request.operatorId());
        
        try {
            // Validar entrada
            log.info("1. Validating request...");
            validateRequest(request);
            log.info("1. Request validated successfully");

            // Obtener vehículo (debe existir previamente)
            log.info("2. Getting vehicle by license plate: {}", request.licensePlate());
            Vehicle vehicle = getVehicleByLicensePlate(request.licensePlate());
            log.info("2. Vehicle found: ID={}, Type={}", vehicle.getId(), vehicle.getVehicleTypeId());

            // Verificar que no tenga sesión activa
            log.info("3. Checking for active session...");
            if (parkingSessionRepository.hasActiveSession(vehicle.getId())) {
                log.warn("3. Vehicle already has active session");
                throw new VehicleAlreadyParkedException(vehicle.getLicensePlate());
            }
            log.info("3. No active session found");

            // Obtener espacio disponible
            log.info("4. Finding available space for vehicle type: {}", vehicle.getVehicleTypeId());
            ParkingSpace availableSpace = findAvailableSpace(vehicle.getVehicleTypeId());
            log.info("4. Available space found: ID={}, Number={}", availableSpace.getId(), availableSpace.getSpaceNumber());

            // Validar operador
            log.info("5. Validating operator: {}", request.operatorId());
            User operator = userRepository.findById(request.operatorId())
                    .orElseThrow(() -> new UserNotFoundException("User with ID '" + request.operatorId() + "' not found"));
            log.info("5. Operator found: ID={}, Name={} {}", operator.getId(), operator.getFirstname(), operator.getLastname());

            // Crear sesión
            log.info("6. Creating parking session...");
            ParkingSession session = new ParkingSession(
                    vehicle.getId(),
                    availableSpace.getId(),
                    operator.getId()
            );
            log.info("6. Session created in memory");

            // Guardar sesión
            log.info("7. Saving session to database...");
            ParkingSession savedSession = parkingSessionRepository.save(session);
            log.info("7. Session saved with ID: {}", savedSession.getId());

            // Generar y asignar ticket code
            log.info("7.5. Generating ticket code...");
            String ticketCode = generateTicketCode(savedSession.getId());
            savedSession.setTicketCode(ticketCode);
            savedSession = parkingSessionRepository.save(savedSession);
            log.info("7.5. Ticket code generated and saved: {}", ticketCode);

            // Marcar espacio como ocupado
            log.info("8. Marking space as occupied...");
            availableSpace.occupy(vehicle.getLicensePlate());
            parkingSpaceRepository.save(availableSpace);
            log.info("8. Space marked as occupied");

            // Obtener información adicional para la respuesta
            log.info("9. Getting additional info...");
            VehicleType vehicleType = vehicleTypeRepository.findById(vehicle.getVehicleTypeId())
                    .orElseThrow(() -> new VehicleTypeNotFoundException(vehicle.getVehicleTypeId()));
            log.info("9. Vehicle type found: {}", vehicleType.getName());

            log.info("Parking session created with ID: {} for vehicle: {}", 
                    savedSession.getId(), vehicle.getLicensePlate());

            StartSessionResponse response = new StartSessionResponse(
                    savedSession.getId(),
                    savedSession.getTicketCode(),
                    vehicle.getLicensePlate(),
                    vehicleType.getName(),
                    availableSpace.getSpaceNumber(),
                    savedSession.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    operator.getFirstname() + " " + operator.getLastname()
            );
            
            log.info("=== PARKING SESSION COMPLETED SUCCESSFULLY ===");
            return response;
            
        } catch (Exception e) {
            log.error("=== ERROR IN PARKING SESSION ===");
            log.error("Error type: {}", e.getClass().getSimpleName());
            log.error("Error message: {}", e.getMessage());
            log.error("Full stack trace:", e);
            throw e; // Re-throw para que la maneje el framework
        }
    }
    
    @Override
    public Class<StartSessionRequest> getRequestType() {
        return StartSessionRequest.class;
    }
    
    private void validateRequest(StartSessionRequest request) {
        if (request.operatorId() == null) {
            throw new BadRequestException("Operator ID is required");
        }
        
        if (request.licensePlate() == null || request.licensePlate().trim().isEmpty()) {
            throw new BadRequestException("License plate is required");
        }
    }
    
    private Vehicle getVehicleByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate.toUpperCase().trim())
                .orElseThrow(() -> new VehicleNotFoundException(licensePlate));
    }
    
    private ParkingSpace findAvailableSpace(Long vehicleTypeId) {
        List<ParkingSpace> availableSpaces = parkingSpaceRepository.findAllAvailableByVehicleType(vehicleTypeId);
        
        if (availableSpaces.isEmpty()) {
            VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
                    .orElseThrow(() -> new VehicleTypeNotFoundException(vehicleTypeId));
            throw new NoAvailableSpaceException(vehicleType.getName());
        }
        
        // Retornar el primer espacio disponible
        return availableSpaces.get(0);
    }
    
    /**
     * Genera un código único para el ticket basado en el ID de sesión
     * Formato: T-YYYYMMDDHHMM-{sessionId}
     */
    private String generateTicketCode(Long sessionId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return String.format("T-%s-%03d", timestamp, sessionId % 1000);
    }
}
