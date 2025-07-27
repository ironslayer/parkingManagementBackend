package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.mapper;

import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession.EndSessionRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession.EndSessionResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession.StartSessionRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession.StartSessionResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.EndSessionRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.EndSessionResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.StartSessionRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.StartSessionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre DTOs de API y objetos de aplicación.
 */
@Mapper(componentModel = "spring")
public interface ParkingSessionDTOMapper {
    
    // Mappers para StartSession
    
    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "operatorId", source = "operatorId")
    StartSessionRequest toStartSessionRequest(StartSessionRequestDTO dto);
    
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "ticketCode", source = "ticketCode")
    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "vehicleType", source = "vehicleType")
    @Mapping(target = "assignedSpace", source = "assignedSpace")
    @Mapping(target = "entryTime", source = "entryTime")
    @Mapping(target = "operatorName", source = "operatorName")
    StartSessionResponseDTO toStartSessionResponseDTO(StartSessionResponse response);
    
    // Mappers para EndSession
    
    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "ticketCode", source = "ticketCode")
    @Mapping(target = "operatorId", source = "operatorId")
    EndSessionRequest toEndSessionRequest(EndSessionRequestDTO dto);
    
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "vehicleType", source = "vehicleType")
    @Mapping(target = "parkingSpace", source = "parkingSpace")
    @Mapping(target = "entryTime", source = "entryTime")
    @Mapping(target = "exitTime", source = "exitTime")
    @Mapping(target = "hoursParked", source = "hoursParked")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "operatorName", source = "operatorName")
    EndSessionResponseDTO toEndSessionResponseDTO(EndSessionResponse response);
}
