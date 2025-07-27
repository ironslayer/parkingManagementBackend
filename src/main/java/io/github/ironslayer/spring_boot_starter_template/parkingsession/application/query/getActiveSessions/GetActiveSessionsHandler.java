package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getActiveSessions;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handler para obtener todas las sesiones activas de parqueo.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetActiveSessionsHandler implements RequestHandler<GetActiveSessionsRequest, GetActiveSessionsResponse> {
    
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    
    @Override
    public GetActiveSessionsResponse handle(GetActiveSessionsRequest request) {
        log.info("Retrieving all active parking sessions");
        
        List<ParkingSession> activeSessions = parkingSessionRepository.findAllActiveSessions();
        
        List<GetActiveSessionsResponse.ActiveSessionDTO> sessionDTOs = activeSessions.stream()
                .map(this::mapToDTO)
                .toList();
        
        log.info("Found {} active parking sessions", sessionDTOs.size());
        
        return new GetActiveSessionsResponse(sessionDTOs, sessionDTOs.size());
    }
    
    @Override
    public Class<GetActiveSessionsRequest> getRequestType() {
        return GetActiveSessionsRequest.class;
    }
    
    private GetActiveSessionsResponse.ActiveSessionDTO mapToDTO(ParkingSession session) {
        // Obtener información del vehículo
        Vehicle vehicle = vehicleRepository.findById(session.getVehicleId())
                .orElse(null);
        
        // Obtener tipo de vehículo
        VehicleType vehicleType = null;
        if (vehicle != null) {
            vehicleType = vehicleTypeRepository.findById(vehicle.getVehicleTypeId())
                    .orElse(null);
        }
        
        // Obtener espacio de parqueo
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(session.getParkingSpaceId())
                .orElse(null);
        
        // Obtener operador
        User operator = userRepository.findById(session.getOperatorEntryId())
                .orElse(null);
        
        // Calcular horas estacionado
        double hoursParked = session.calculateParkedHours();
        String hoursDisplay = BigDecimal.valueOf(hoursParked)
                .setScale(2, RoundingMode.HALF_UP)
                .toString() + " hours";
        
        return new GetActiveSessionsResponse.ActiveSessionDTO(
                session.getId(),
                vehicle != null ? vehicle.getLicensePlate() : "Unknown",
                vehicleType != null ? vehicleType.getName() : "Unknown",
                parkingSpace != null ? parkingSpace.getSpaceNumber() : "Unknown",
                session.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                hoursDisplay,
                operator != null ? operator.getFirstname() + " " + operator.getLastname() : "Unknown"
        );
    }
}
