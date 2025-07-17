package io.github.ironslayer.spring_boot_starter_template.user.application.command.deleteUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteUserHandler implements RequestHandler<DeleteUserRequest, Void> {

    private final UserRepository userRepository;

    @Override
    public Void handle(DeleteUserRequest request) {

        userRepository.deleteById(request.userId());

        return null;
    }

    @Override
    public Class<DeleteUserRequest> getRequestType() {
        return DeleteUserRequest.class;
    }
}
