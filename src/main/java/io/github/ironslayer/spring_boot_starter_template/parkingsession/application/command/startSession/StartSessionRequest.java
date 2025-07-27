package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para iniciar una nueva sesión de parqueo.
 * En el flujo real, el operador solo ingresa la placa del vehículo.
 * El sistema automáticamente busca o registra el vehículo.
 */
public record StartSessionRequest(
    String licensePlate,
    Long operatorId
) implements Request<StartSessionResponse> {}
