package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.update;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;

/**
 * Request para actualizar un vehículo existente.
 */
public record UpdateVehicleRequest(
        Vehicle vehicle
) implements Request<Void> {
}
