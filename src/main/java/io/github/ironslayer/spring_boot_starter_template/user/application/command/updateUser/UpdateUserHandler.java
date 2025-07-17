package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUserHandler implements RequestHandler<UpdateUserRequest, Void> {

    private final UserRepository userRepository;

    @Override
    public Void handle(UpdateUserRequest request) {

        User user = userRepository.findById(request.user().getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFirstname(request.user().getFirstname());
        user.setLastname(request.user().getLastname());

        userRepository.save(user);

        return null;
    }

    @Override
    public Class<UpdateUserRequest> getRequestType() {
        return UpdateUserRequest.class;
    }
}
