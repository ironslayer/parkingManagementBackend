package io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserHandler implements RequestHandler<GetUserRequest, GetUserResponse> {

    private final UserRepository userRepository;

    @Override
    public GetUserResponse handle(GetUserRequest request) {

        User user = userRepository.findById(request.userId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        return new GetUserResponse(user);
    }

    @Override
    public Class<GetUserRequest> getRequestType() {
        return GetUserRequest.class;
    }
}
