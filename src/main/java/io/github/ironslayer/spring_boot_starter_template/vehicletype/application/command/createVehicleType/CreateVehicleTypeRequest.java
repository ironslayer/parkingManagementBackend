package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.createVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;

/**
 * Request para crear un nuevo tipo de vehículo
 * Solo usuarios con rol ADMIN pueden ejecutar esta operación
 */
public record CreateVehicleTypeRequest(VehicleType vehicleType) implements Request<CreateVehicleTypeResponse> {
}
