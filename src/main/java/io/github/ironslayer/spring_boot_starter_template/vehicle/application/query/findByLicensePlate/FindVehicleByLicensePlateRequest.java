package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.findByLicensePlate;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para buscar un vehículo por placa.
 */
public record FindVehicleByLicensePlateRequest(
        String licensePlate
) implements Request<FindVehicleByLicensePlateResponse> {
}
