package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUserPartial;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para actualizar parcialmente un usuario
 * Solo los campos no-null serán actualizados
 */
public record UpdateUserPartialRequest(
    Long userId,
    String firstname,
    String lastname,
    String email,
    String role
) implements Request<Void> {
}
