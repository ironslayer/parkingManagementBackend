package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfig;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener una configuración de tarifa por ID
 */
public record GetRateConfigRequest(Long rateConfigId) implements Request<GetRateConfigResponse> {
}
