package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getAllVehicles;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener todos los vehículos.
 */
public record GetAllVehiclesRequest(
        Boolean activeOnly // Si es true, solo devuelve vehículos activos
) implements Request<GetAllVehiclesResponse> {
}
