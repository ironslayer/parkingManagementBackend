package io.github.ironslayer.spring_boot_starter_template.user.application.command.registerOperator;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.config.application.JwtService;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RegisterOperatorHandler implements RequestHandler<RegisterOperatorRequest, RegisterOperatorResponse> {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public RegisterOperatorResponse handle(RegisterOperatorRequest request) {

        User user = request.user();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Error: User with this email already exists");
        }

        // Configurar usuario como OPERATOR
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.OPERATOR);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        UserEntity userEntity = userMapper.userToUserEntity(saved);

        String jwtToken = jwtService.generateToken(userEntity);

        return new RegisterOperatorResponse(jwtToken);
    }

    @Override
    public Class<RegisterOperatorRequest> getRequestType() {
        return RegisterOperatorRequest.class;
    }
}
