package io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

public record GetAllUsersRequest() implements Request<GetAllUsersResponse> {
}
