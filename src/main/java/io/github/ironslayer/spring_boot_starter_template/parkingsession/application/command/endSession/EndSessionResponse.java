package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession;

import java.math.BigDecimal;

/**
 * Response del comando de finalizar sesión de parqueo.
 * Contiene la información del recibo y tiempo estacionado.
 */
public record EndSessionResponse(
    Long sessionId,
    String licensePlate,
    String vehicleType,
    String parkingSpace,
    String entryTime,
    String exitTime,
    BigDecimal hoursParked,
    BigDecimal totalAmount,
    String operatorName
) {}
