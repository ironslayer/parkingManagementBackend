package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfigByVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener la configuración de tarifa activa por tipo de vehículo
 */
public record GetRateConfigByVehicleTypeRequest(Long vehicleTypeId) implements Request<GetRateConfigByVehicleTypeResponse> {
}
