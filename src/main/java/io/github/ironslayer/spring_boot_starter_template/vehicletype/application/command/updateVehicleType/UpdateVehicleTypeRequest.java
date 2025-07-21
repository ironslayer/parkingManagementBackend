package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.updateVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;

/**
 * Request para actualizar un tipo de vehículo existente
 * Solo usuarios con rol ADMIN pueden ejecutar esta operación
 */
public record UpdateVehicleTypeRequest(VehicleType vehicleType) implements Request<Void> {
}
