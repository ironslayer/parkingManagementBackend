package io.github.ironslayer.spring_boot_starter_template.user.application.query.getCurrentUserProfile;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

public record GetCurrentUserProfileRequest() implements Request<GetCurrentUserProfileResponse> {
}
