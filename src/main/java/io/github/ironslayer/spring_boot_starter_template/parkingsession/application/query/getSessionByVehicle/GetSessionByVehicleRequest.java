package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getSessionByVehicle;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener la sesión activa de un vehículo específico.
 */
public record GetSessionByVehicleRequest(
    String licensePlate
) implements Request<GetSessionByVehicleResponse> {
}
