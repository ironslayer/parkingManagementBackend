package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getAllRateConfigs;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;

import java.util.List;

/**
 * Response que contiene todas las configuraciones de tarifas
 */
public record GetAllRateConfigsResponse(List<RateConfig> rateConfigs) {
}
