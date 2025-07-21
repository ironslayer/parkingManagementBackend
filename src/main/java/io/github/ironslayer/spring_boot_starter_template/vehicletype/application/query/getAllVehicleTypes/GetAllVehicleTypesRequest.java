package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getAllVehicleTypes;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener todos los tipos de vehículos
 */
public record GetAllVehicleTypesRequest() implements Request<GetAllVehicleTypesResponse> {
}
