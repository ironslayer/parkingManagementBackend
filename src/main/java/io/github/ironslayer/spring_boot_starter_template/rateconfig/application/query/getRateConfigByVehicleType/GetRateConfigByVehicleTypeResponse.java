package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfigByVehicleType;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;

/**
 * Response que contiene la configuración de tarifa activa para un tipo de vehículo
 */
public record GetRateConfigByVehicleTypeResponse(RateConfig rateConfig) {
}
