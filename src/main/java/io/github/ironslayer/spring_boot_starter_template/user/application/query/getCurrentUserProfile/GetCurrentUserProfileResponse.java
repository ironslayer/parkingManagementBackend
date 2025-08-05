package io.github.ironslayer.spring_boot_starter_template.user.application.query.getCurrentUserProfile;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;

public record GetCurrentUserProfileResponse(User user) {
}
