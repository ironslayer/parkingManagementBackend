package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener un tipo de vehículo por ID
 */
public record GetVehicleTypeRequest(Long vehicleTypeId) implements Request<GetVehicleTypeResponse> {
}
