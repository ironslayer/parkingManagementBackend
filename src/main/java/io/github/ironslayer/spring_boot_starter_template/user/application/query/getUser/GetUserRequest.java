package io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

public record GetUserRequest(Long userId) implements Request<GetUserResponse> {
}
