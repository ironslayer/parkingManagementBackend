package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de vehículos.
 */
public record CreateVehicleRequestDTO(
        
        @NotBlank(message = "License plate is required")
        @Size(min = 3, max = 15, message = "License plate must be between 3 and 15 characters")
        @JsonProperty("licensePlate")
        String licensePlate,
        
        @NotNull(message = "Vehicle type ID is required")
        @Positive(message = "Vehicle type ID must be positive")
        @JsonProperty("vehicleTypeId")
        Long vehicleTypeId,
        
        @Size(max = 50, message = "Brand cannot exceed 50 characters")
        @JsonProperty("brand")
        String brand,
        
        @Size(max = 50, message = "Model cannot exceed 50 characters")
        @JsonProperty("model")
        String model,
        
        @Size(max = 30, message = "Color cannot exceed 30 characters")
        @JsonProperty("color")
        String color,
        
        @Size(max = 100, message = "Owner name cannot exceed 100 characters")
        @JsonProperty("ownerName")
        String ownerName,
        
        @Size(max = 20, message = "Owner phone cannot exceed 20 characters")
        @JsonProperty("ownerPhone")
        String ownerPhone
) {
}
