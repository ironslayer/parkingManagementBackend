package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing parking space via PATCH.
 * All fields are optional for partial updates.
 * The ID is taken from the URL path parameter.
 */
public record UpdateParkingSpaceRequestDTO(
        
        @Size(min = 1, max = 10, message = "Space number must be between 1 and 10 characters")
        @JsonProperty("spaceNumber")
        String spaceNumber,
        
        @Positive(message = "Vehicle type ID must be positive")
        @JsonProperty("vehicleTypeId")
        Long vehicleTypeId,
        
        @JsonProperty("isActive")
        Boolean isActive
) {
}
