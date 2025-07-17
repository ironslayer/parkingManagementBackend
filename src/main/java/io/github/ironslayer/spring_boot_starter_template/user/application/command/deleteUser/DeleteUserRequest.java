package io.github.ironslayer.spring_boot_starter_template.user.application.command.deleteUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

public record DeleteUserRequest(Long userId) implements Request<Void> {
}
