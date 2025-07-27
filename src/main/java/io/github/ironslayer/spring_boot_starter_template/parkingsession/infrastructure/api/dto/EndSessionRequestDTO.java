package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitud de finalización de sesión de parqueo.
 */
@Schema(description = "Request to end a parking session")
public record EndSessionRequestDTO(
    
    @Schema(description = "Vehicle license plate", example = "ABC-123")
    String licensePlate,
    
    @Schema(description = "Session ID (alternative to license plate)", example = "1")
    Long sessionId,
    
    @Schema(description = "Ticket code (alternative to license plate and session ID)", example = "T-202507171030-001")
    String ticketCode,
    
    @NotNull(message = "Operator ID is required")
    @Schema(description = "ID of the operator registering the exit", example = "2", required = true)
    Long operatorId
) {}
