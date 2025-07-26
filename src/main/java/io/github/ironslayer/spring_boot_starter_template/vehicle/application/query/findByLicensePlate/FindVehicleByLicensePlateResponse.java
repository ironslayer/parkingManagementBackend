package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.findByLicensePlate;

import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;

/**
 * Response para buscar vehículo por placa.
 */
public record FindVehicleByLicensePlateResponse(
        Vehicle vehicle
) {
}
