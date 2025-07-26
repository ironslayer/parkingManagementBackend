package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.create;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;

/**
 * Request para crear un nuevo vehículo.
 */
public record CreateVehicleRequest(
        Vehicle vehicle
) implements Request<CreateVehicleResponse> {
}
