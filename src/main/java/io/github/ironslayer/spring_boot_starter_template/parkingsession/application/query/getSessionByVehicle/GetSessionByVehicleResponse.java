package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getSessionByVehicle;

import java.math.BigDecimal;

/**
 * Response con la información de la sesión de un vehículo específico.
 */
public record GetSessionByVehicleResponse(
    Long sessionId,
    String licensePlate,
    String vehicleType,
    String parkingSpace,
    String entryTime,
    BigDecimal hoursParked,
    BigDecimal estimatedAmount,
    String operatorName,
    String ticketCode
) {}
