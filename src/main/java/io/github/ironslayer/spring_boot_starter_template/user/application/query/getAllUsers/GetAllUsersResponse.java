package io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;

import java.util.List;

public record GetAllUsersResponse(List<User> users) {
}
