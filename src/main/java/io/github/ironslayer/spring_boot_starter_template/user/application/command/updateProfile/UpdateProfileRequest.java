package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateProfile;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para actualizar el perfil personal del usuario autenticado
 * Solo los campos no-null serán actualizados
 */
public record UpdateProfileRequest(
    String firstname,
    String lastname,
    String currentPassword,
    String newPassword
) implements Request<Void> {
}
