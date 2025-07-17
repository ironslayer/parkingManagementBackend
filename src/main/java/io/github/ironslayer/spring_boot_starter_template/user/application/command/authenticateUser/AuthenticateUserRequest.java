package io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

public record AuthenticateUserRequest(String email, String password) implements Request<AuthenticateUserResponse> {
}
