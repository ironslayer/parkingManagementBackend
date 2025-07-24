package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new parking space.
 */
public record CreateParkingSpaceRequestDTO(
        
        @NotBlank(message = "Space number is required")
        @Size(min = 1, max = 10, message = "Space number must be between 1 and 10 characters")
        @JsonProperty("spaceNumber")
        String spaceNumber,
        
        @NotNull(message = "Vehicle type ID is required")
        @Positive(message = "Vehicle type ID must be positive")
        @JsonProperty("vehicleTypeId")
        Long vehicleTypeId
) {
}
