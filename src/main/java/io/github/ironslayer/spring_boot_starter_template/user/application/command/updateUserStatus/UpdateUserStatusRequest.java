package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUserStatus;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import lombok.NonNull;

/**
 * Request para cambiar el estado (activo/inactivo) de un usuario
 * Implementa el patrón Mediator siguiendo la arquitectura hexagonal
 * 
 * @param userId ID del usuario a modificar
 * @param isActive nuevo estado del usuario
 */
public record UpdateUserStatusRequest(
    @NonNull Long userId,
    @NonNull Boolean isActive
) implements Request<Void> {
}
