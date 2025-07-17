package io.github.ironslayer.spring_boot_starter_template.user.application.command.registerUser;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.config.application.JwtService;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterUserHandler implements RequestHandler<RegisterUserRequest, RegisterUserResponse> {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public RegisterUserResponse handle(RegisterUserRequest request) {

        User user = request.user();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Error the user exist");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.OPERATOR); // Por defecto los nuevos registros son OPERATOR (empleados del parqueadero)
        user.setIsActive(true);

        User saved = userRepository.save(user);
        UserEntity userEntity = userMapper.userToUserEntity(saved);

        String jwtToken = jwtService.generateToken(userEntity);

        return new RegisterUserResponse(jwtToken);
    }

    @Override
    public Class<RegisterUserRequest> getRequestType() {
        return RegisterUserRequest.class;
    }
}
