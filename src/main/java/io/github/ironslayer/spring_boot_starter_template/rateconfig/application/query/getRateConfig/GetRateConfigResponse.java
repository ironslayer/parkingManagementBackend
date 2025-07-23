package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfig;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;

/**
 * Response que contiene la configuración de tarifa solicitada
 */
public record GetRateConfigResponse(RateConfig rateConfig) {
}
