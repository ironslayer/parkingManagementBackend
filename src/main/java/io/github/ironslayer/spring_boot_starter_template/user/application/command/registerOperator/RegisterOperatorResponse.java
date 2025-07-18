package io.github.ironslayer.spring_boot_starter_template.user.application.command.registerOperator;

/**
 * Response para el registro exitoso de un operador
 * Contiene el JWT token para autenticación inmediata
 */
public record RegisterOperatorResponse(String jwt) {
}
