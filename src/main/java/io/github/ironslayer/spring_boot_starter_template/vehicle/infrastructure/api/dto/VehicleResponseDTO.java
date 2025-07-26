package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para vehículos.
 */
public record VehicleResponseDTO(
        
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("licensePlate")
        String licensePlate,
        
        @JsonProperty("vehicleTypeId")
        Long vehicleTypeId,
        
        @JsonProperty("brand")
        String brand,
        
        @JsonProperty("model")
        String model,
        
        @JsonProperty("color")
        String color,
        
        @JsonProperty("ownerName")
        String ownerName,
        
        @JsonProperty("ownerPhone")
        String ownerPhone,
        
        @JsonProperty("isActive")
        Boolean isActive,
        
        @JsonProperty("createdAt")
        LocalDateTime createdAt,
        
        @JsonProperty("updatedAt")
        LocalDateTime updatedAt
) {
}
