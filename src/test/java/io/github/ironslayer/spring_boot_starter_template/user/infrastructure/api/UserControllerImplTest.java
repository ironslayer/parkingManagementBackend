package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser.AuthenticateUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.registerUser.RegisterUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers.GetAllUsersResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser.GetUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticatedUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticationUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.RegisterUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    @Mock
    private Mediator mediator;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserControllerImpl controller;

    private final User user = User.builder().id(1L).build();
    private final UserDTO userDTO = UserDTO.builder().id(1L).build();

    @Test
    void findById() {

        when(mediator.dispatch(any())).thenReturn(new GetUserResponse(user));
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.findById(1L);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void findAll() {
        when(mediator.dispatch(any())).thenReturn(new GetAllUsersResponse(List.of(user)));
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        ResponseEntity<List<UserDTO>> response = controller.findAll();
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void update() {

        ResponseEntity<Void> response = controller.update(userDTO);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
    }

    @Test
    void delete() {
        ResponseEntity<Void> response = controller.delete(1L);
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
    }

    @Test
    void register() {
        when(mediator.dispatch(any())).thenReturn(new RegisterUserResponse("12345"));
        when(userMapper.registerUserDTOToUser(any())).thenReturn(user);

        RegisterUserDTO userRegisterDTO = RegisterUserDTO.builder().build();

        ResponseEntity<AuthenticatedUserDTO> response = controller.register(userRegisterDTO);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void authenticate() {

        when(mediator.dispatch(any())).thenReturn(new AuthenticateUserResponse("12345"));

        AuthenticationUserDTO authenticationDTO = AuthenticationUserDTO.builder().build();

        ResponseEntity<AuthenticatedUserDTO> response = controller.authenticate(authenticationDTO);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }
}