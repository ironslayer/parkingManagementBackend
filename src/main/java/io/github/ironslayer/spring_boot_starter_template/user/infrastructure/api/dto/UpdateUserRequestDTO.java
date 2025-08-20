package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar parcialmente un usuario (ADMIN)
 * Todos los campos son opcionales - solo se actualizan los campos enviados
 * 
 * @param firstname Nuevo nombre (opcional)
 * @param lastname Nuevo apellido (opcional)
 * @param email Nuevo email (opcional, debe ser único)
 * @param role Nuevo rol (opcional, validaciones especiales)
 */
@Schema(description = "Request DTO para actualizar parcialmente un usuario (ADMIN only)")
public record UpdateUserRequestDTO(
    
    @Size(min = 1, max = 50, message = "Firstname must be between 1 and 50 characters")
    @Schema(description = "Nuevo nombre del usuario", example = "Juan Carlos")
    String firstname,
    
    @Size(min = 1, max = 50, message = "Lastname must be between 1 and 50 characters")
    @Schema(description = "Nuevo apellido del usuario", example = "González")
    String lastname,
    
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Nuevo email del usuario", example = "nuevo@email.com")
    String email,
    
    @Schema(description = "Nuevo rol del usuario", example = "ADMIN", allowableValues = {"ADMIN", "OPERATOR"})
    String role
    
) {
}
