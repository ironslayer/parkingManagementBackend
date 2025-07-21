package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getAllVehicleTypes;

import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;

import java.util.List;

/**
 * Response con la lista de todos los tipos de vehículos
 */
public record GetAllVehicleTypesResponse(List<VehicleType> vehicleTypes) {
}
