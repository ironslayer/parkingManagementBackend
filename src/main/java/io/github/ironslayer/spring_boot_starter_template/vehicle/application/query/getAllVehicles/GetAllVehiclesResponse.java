package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getAllVehicles;

import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;

import java.util.List;

/**
 * Response para obtener todos los vehículos.
 */
public record GetAllVehiclesResponse(
        List<Vehicle> vehicles
) {
}
