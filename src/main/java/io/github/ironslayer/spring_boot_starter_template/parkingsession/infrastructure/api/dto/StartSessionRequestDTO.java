package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitud de inicio de sesión de parqueo.
 */
@Schema(description = "Request to start a parking session")
public record StartSessionRequestDTO(
    
    @NotNull(message = "License plate is required")
    @Schema(description = "Vehicle license plate", example = "ABC-123", required = true)
    String licensePlate,
    
    @NotNull(message = "Operator ID is required")
    @Schema(description = "ID of the operator registering the entry", example = "2", required = true)
    Long operatorId
) {}
