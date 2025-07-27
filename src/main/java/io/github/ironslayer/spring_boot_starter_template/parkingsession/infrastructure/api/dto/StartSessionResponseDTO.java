package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respuesta de inicio de sesión de parqueo.
 */
@Schema(description = "Response when starting a parking session")
public record StartSessionResponseDTO(
    
    @Schema(description = "Generated session ID", example = "1")
    Long sessionId,
    
    @Schema(description = "Generated ticket code", example = "T-202507171030-001")
    String ticketCode,
    
    @Schema(description = "Vehicle license plate", example = "ABC-123")
    String licensePlate,
    
    @Schema(description = "Vehicle type", example = "CAR")
    String vehicleType,
    
    @Schema(description = "Assigned parking space", example = "A-001")
    String assignedSpace,
    
    @Schema(description = "Entry time", example = "2025-07-17 10:30:00")
    String entryTime,
    
    @Schema(description = "Operator name who registered the entry", example = "Juan Pérez")
    String operatorName
) {}
