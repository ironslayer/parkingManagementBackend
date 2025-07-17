package io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUsersHandler implements RequestHandler<GetAllUsersRequest, GetAllUsersResponse> {

    private final UserRepository userRepository;

    @Override
    public GetAllUsersResponse handle(GetAllUsersRequest request) {

        List<User> users = userRepository.findAll();

        return new GetAllUsersResponse(users);
    }

    @Override
    public Class<GetAllUsersRequest> getRequestType() {
        return GetAllUsersRequest.class;
    }
}
