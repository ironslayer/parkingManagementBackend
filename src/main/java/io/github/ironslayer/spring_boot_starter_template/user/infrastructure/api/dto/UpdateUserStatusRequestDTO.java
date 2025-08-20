package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el estado (activo/inactivo) de un usuario
 * Sigue el mismo patrón que ChangeVehicleStatusRequestDTO para consistencia
 * 
 * @param isActive el nuevo estado del usuario (true = activo, false = inactivo)
 */
@Schema(description = "Request DTO para cambiar el estado de un usuario")
public record UpdateUserStatusRequestDTO(
    
    @NotNull(message = "isActive field is required")
    @Schema(description = "Nuevo estado del usuario", example = "false", required = true)
    Boolean isActive
) {
}
