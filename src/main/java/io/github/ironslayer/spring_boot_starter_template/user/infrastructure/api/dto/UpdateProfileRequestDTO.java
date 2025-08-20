package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar el perfil personal del usuario autenticado
 * Todos los campos son opcionales - solo se actualizan los campos enviados
 * 
 * @param firstname Nuevo nombre (opcional)
 * @param lastname Nuevo apellido (opcional)
 * @param currentPassword Contraseña actual (solo si se cambia password)
 * @param newPassword Nueva contraseña (solo si se quiere cambiar)
 */
@Schema(description = "Request DTO para actualizar perfil personal")
public record UpdateProfileRequestDTO(
    
    @Size(min = 1, max = 50, message = "Firstname must be between 1 and 50 characters")
    @Schema(description = "Nuevo nombre", example = "Carlos Alberto")
    String firstname,
    
    @Size(min = 1, max = 50, message = "Lastname must be between 1 and 50 characters")
    @Schema(description = "Nuevo apellido", example = "Martínez")
    String lastname,
    
    @Size(min = 5, message = "Current password must be at least 5 characters")
    @Schema(description = "Contraseña actual (requerida si se cambia contraseña)", example = "currentPassword123")
    String currentPassword,
    
    @Size(min = 5, message = "New password must be at least 5 characters")
    @Schema(description = "Nueva contraseña (opcional)", example = "newPassword123")
    String newPassword
    
) {
}
