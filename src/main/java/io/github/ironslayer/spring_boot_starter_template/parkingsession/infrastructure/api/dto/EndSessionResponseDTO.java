package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO para respuesta de finalización de sesión de parqueo.
 */
@Schema(description = "Response when ending a parking session")
public record EndSessionResponseDTO(
    
    @Schema(description = "Session ID", example = "1")
    Long sessionId,
    
    @Schema(description = "Vehicle license plate", example = "ABC-123")
    String licensePlate,
    
    @Schema(description = "Vehicle type", example = "CAR")
    String vehicleType,
    
    @Schema(description = "Parking space", example = "A-001")
    String parkingSpace,
    
    @Schema(description = "Entry time", example = "2025-07-17 10:30:00")
    String entryTime,
    
    @Schema(description = "Exit time", example = "2025-07-17 12:30:00")
    String exitTime,
    
    @Schema(description = "Hours parked", example = "2.00")
    BigDecimal hoursParked,
    
    @Schema(description = "Total amount to pay", example = "4000.00")
    BigDecimal totalAmount,
    
    @Schema(description = "Operator name who registered the exit", example = "María García")
    String operatorName
) {}
