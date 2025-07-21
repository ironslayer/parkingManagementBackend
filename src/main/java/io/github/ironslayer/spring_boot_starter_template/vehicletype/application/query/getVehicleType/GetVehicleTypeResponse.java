package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getVehicleType;

import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;

/**
 * Response con el tipo de vehículo encontrado
 */
public record GetVehicleTypeResponse(VehicleType vehicleType) {
}
