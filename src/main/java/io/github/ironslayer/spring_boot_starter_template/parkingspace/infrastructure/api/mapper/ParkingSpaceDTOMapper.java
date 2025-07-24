package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.mapper;

import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for converting between ParkingSpace domain entities and API DTOs.
 */
@Mapper(componentModel = "spring")
public interface ParkingSpaceDTOMapper {
    
    /**
     * Convert CreateParkingSpaceRequestDTO to domain entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "occupied", constant = "false")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "occupiedByVehiclePlate", ignore = true)
    @Mapping(target = "occupiedAt", ignore = true)
    ParkingSpace toDomain(CreateParkingSpaceRequestDTO dto);
    
    /**
     * Convert UpdateParkingSpaceRequestDTO to domain entity.
     * Only maps non-null fields for partial updates.
     * ID is handled separately from URL path parameter.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "occupied", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "occupiedByVehiclePlate", ignore = true)
    @Mapping(target = "occupiedAt", ignore = true)
    @Mapping(source = "isActive", target = "active")
    ParkingSpace toDomain(UpdateParkingSpaceRequestDTO dto);
    
    /**
     * Convert domain entity to response DTO.
     */
    @Mapping(source = "occupied", target = "isOccupied")
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "available", target = "isAvailable")
    ParkingSpaceResponseDTO toResponseDTO(ParkingSpace parkingSpace);
    
    /**
     * Convert list of domain entities to list of response DTOs.
     */
    List<ParkingSpaceResponseDTO> toResponseDTOList(List<ParkingSpace> parkingSpaces);
    
    /**
     * Create AvailableSpacesResponseDTO from parking spaces list and metadata.
     */
    default AvailableSpacesResponseDTO toAvailableSpacesResponseDTO(
            List<ParkingSpace> availableSpaces, 
            long totalCount, 
            Long vehicleTypeId) {
        List<ParkingSpaceResponseDTO> spaceDTOs = toResponseDTOList(availableSpaces);
        return new AvailableSpacesResponseDTO(spaceDTOs, totalCount, vehicleTypeId);
    }
}
