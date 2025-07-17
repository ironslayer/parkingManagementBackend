package io.github.ironslayer.spring_boot_starter_template.user.application.command.registerUser;

import io.github.ironslayer.spring_boot_starter_template.config.application.JwtService;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserHandlerTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterUserHandler handler;

    @Test
    void handle() {

        RegisterUserRequest request = new RegisterUserRequest(User.builder().email("test@gmail.com").password("12345").build());
        String jwt = "12345";

        when(userRepository.existsByEmail(request.user().getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.user().getPassword())).thenReturn("encodedPass");

        User savedUser = User.builder().build();
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPass");

        when(userRepository.save(request.user())).thenReturn(savedUser);

        UserEntity userEntity = new UserEntity();
        when(userMapper.userToUserEntity(savedUser)).thenReturn(userEntity);
        when(jwtService.generateToken(userEntity)).thenReturn(jwt);


        RegisterUserResponse response = handler.handle(request);
        assertEquals(jwt, response.jwt());
    }

    @Test
    void handleErrorUserExist() {

        RegisterUserRequest request = new RegisterUserRequest(User.builder().email("test@gmail.com").password("12345").build());

        when(userRepository.existsByEmail(request.user().getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> handler.handle(request));
    }

    @Test
    void getRequestType() {
        assertEquals(RegisterUserRequest.class, handler.getRequestType());
    }

}