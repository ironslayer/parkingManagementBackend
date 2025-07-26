package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.delete;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para eliminar (desactivar) un vehículo.
 */
public record DeleteVehicleRequest(
        Long vehicleId
) implements Request<Void> {
}
