package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitud de cambio de estado de vehículo.
 * Permite activar o desactivar un vehículo.
 */
@Schema(description = "Request to change vehicle status (activate/deactivate)")
public record ChangeVehicleStatusRequestDTO(
        
        @Schema(description = "New status for the vehicle", example = "true")
        @NotNull(message = "Status is required")
        Boolean isActive
) {
}
