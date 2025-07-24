package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO for parking space responses.
 * Contains all parking space information for API responses.
 */
public record ParkingSpaceResponseDTO(
        
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("spaceNumber")
        String spaceNumber,
        
        @JsonProperty("vehicleTypeId")
        Long vehicleTypeId,
        
        @JsonProperty("isOccupied")
        boolean isOccupied,
        
        @JsonProperty("isActive")
        boolean isActive,
        
        @JsonProperty("createdAt")
        LocalDateTime createdAt,
        
        @JsonProperty("updatedAt")
        LocalDateTime updatedAt,
        
        @JsonProperty("occupiedByVehiclePlate")
        String occupiedByVehiclePlate,
        
        @JsonProperty("occupiedAt")
        LocalDateTime occupiedAt,
        
        @JsonProperty("isAvailable")
        boolean isAvailable
) {
}
