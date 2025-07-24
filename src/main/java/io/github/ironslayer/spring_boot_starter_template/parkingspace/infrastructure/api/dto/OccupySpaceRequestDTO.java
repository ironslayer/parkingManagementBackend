package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for occupying a parking space.
 * The parking space ID is taken from the URL path parameter.
 */
public record OccupySpaceRequestDTO(
        
        @NotBlank(message = "Vehicle plate is required")
        @Size(min = 3, max = 15, message = "Vehicle plate must be between 3 and 15 characters")
        @JsonProperty("vehiclePlate")
        String vehiclePlate
) {
}
