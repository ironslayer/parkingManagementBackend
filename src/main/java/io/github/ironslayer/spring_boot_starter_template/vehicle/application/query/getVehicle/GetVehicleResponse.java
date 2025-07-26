package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getVehicle;

import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;

/**
 * Response para obtener un vehículo.
 */
public record GetVehicleResponse(
        Vehicle vehicle
) {
}
