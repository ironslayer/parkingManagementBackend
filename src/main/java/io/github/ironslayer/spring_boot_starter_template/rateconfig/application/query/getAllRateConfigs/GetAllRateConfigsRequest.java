package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getAllRateConfigs;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener todas las configuraciones de tarifas
 */
public record GetAllRateConfigsRequest() implements Request<GetAllRateConfigsResponse> {
}
