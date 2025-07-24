package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for available spaces response with summary information.
 */
public record AvailableSpacesResponseDTO(
        
        @JsonProperty("availableSpaces")
        List<ParkingSpaceResponseDTO> availableSpaces,
        
        @JsonProperty("totalCount")
        long totalCount,
        
        @JsonProperty("vehicleTypeId")
        Long vehicleTypeId // null if not filtered by vehicle type
) {
}
