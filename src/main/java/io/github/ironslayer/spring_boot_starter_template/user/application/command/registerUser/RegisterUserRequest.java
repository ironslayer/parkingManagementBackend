package io.github.ironslayer.spring_boot_starter_template.user.application.command.registerUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;

public record RegisterUserRequest(User user) implements Request<RegisterUserResponse> {
}
