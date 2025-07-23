package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.updateRateConfig;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;

/**
 * Request para actualizar una configuración de tarifa existente
 * Solo usuarios con rol ADMIN pueden ejecutar esta operación
 */
public record UpdateRateConfigRequest(RateConfig rateConfig) implements Request<Void> {
}
