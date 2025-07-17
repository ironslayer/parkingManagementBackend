package io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.config.application.JwtService;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticateUserHandler implements RequestHandler<AuthenticateUserRequest, AuthenticateUserResponse> {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public AuthenticateUserResponse handle(AuthenticateUserRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserEntity userEntity = userMapper.userToUserEntity(user);

        String jwtToken = jwtService.generateToken(userEntity);

        return new AuthenticateUserResponse(jwtToken);
    }

    @Override
    public Class<AuthenticateUserRequest> getRequestType() {
        return AuthenticateUserRequest.class;
    }
}
