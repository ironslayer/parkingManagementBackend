package io.github.ironslayer.spring_boot_starter_template.user.application.command.registerOperator;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;

/**
 * Request para registrar un nuevo operador del parqueo
 * Solo los usuarios con rol ADMIN pueden ejecutar esta operación
 */
public record RegisterOperatorRequest(User user) implements Request<RegisterOperatorResponse> {
}
