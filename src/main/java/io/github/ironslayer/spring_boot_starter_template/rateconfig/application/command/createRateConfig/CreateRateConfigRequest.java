package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.createRateConfig;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;

/**
 * Request para crear una nueva configuración de tarifa
 * Solo usuarios con rol ADMIN pueden ejecutar esta operación
 */
public record CreateRateConfigRequest(RateConfig rateConfig) implements Request<CreateRateConfigResponse> {
}
