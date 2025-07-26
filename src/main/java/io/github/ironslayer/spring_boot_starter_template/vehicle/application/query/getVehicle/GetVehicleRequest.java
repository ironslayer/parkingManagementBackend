package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getVehicle;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener un vehículo por ID.
 */
public record GetVehicleRequest(
        Long vehicleId
) implements Request<GetVehicleResponse> {
}
