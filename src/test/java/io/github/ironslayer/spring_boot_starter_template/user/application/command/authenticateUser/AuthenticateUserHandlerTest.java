package io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser;

import io.github.ironslayer.spring_boot_starter_template.config.application.JwtService;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserHandlerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticateUserHandler handler;

    @Test
    void handle() {
        AuthenticateUserRequest request = new AuthenticateUserRequest("test@gmail.com", "password");
        User user = User.builder().build();
        UserEntity userEntity = new UserEntity();
        String jwt = "12345";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.userToUserEntity(user)).thenReturn(userEntity);
        when(jwtService.generateToken(userEntity)).thenReturn(jwt);

        AuthenticateUserResponse response = handler.handle(request);

        assertEquals(jwt, response.jwt());
    }

    @Test
    void getRequestType() {
        assertEquals(AuthenticateUserRequest.class, handler.getRequestType());
    }
}