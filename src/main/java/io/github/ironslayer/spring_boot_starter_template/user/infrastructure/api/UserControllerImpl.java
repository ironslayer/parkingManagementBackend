package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api;


import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser.AuthenticateUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser.AuthenticateUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.deleteUser.DeleteUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.registerUser.RegisterUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.registerUser.RegisterUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUser.UpdateUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers.GetAllUsersRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers.GetAllUsersResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser.GetUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser.GetUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticatedUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticationUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.RegisterUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User", description = "The User API. Contains all the operations that can be performed on a user.")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserControllerImpl implements UserController {

    private final Mediator mediator;
    private final UserMapper userMapper;

    @Operation(summary = "Find a user", description = "Find a user")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {

        GetUserResponse response = mediator.dispatch(new GetUserRequest(id));

        UserDTO userDTO = userMapper.userToUserDTO(response.user());

        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "List all users", description = "List all users")
    @GetMapping()
    public ResponseEntity<List<UserDTO>> findAll() {

        log.info("UserController Get all users");

        GetAllUsersResponse response = mediator.dispatch(new GetAllUsersRequest());

        List<UserDTO> userDTOS = response.users().stream().map(userMapper::userToUserDTO).toList();

        return ResponseEntity.ok(userDTOS);
    }

    @Operation(summary = "Update a user", description = "Update a user")
    @PutMapping()
    public ResponseEntity<Void> update(@RequestBody UserDTO userDTO) {

        User user = userMapper.userDTOToUser(userDTO);

        mediator.dispatch(new UpdateUserRequest(user));

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a user", description = "Delete a user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        mediator.dispatch(new DeleteUserRequest(id));

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Register a user", description = "Register a user")
    @PostMapping("/register")
    public ResponseEntity<AuthenticatedUserDTO> register(@RequestBody @Validated RegisterUserDTO request) {

        User user = userMapper.registerUserDTOToUser(request);

        RegisterUserResponse response = mediator.dispatch(new RegisterUserRequest(user));

        return ResponseEntity.ok(new AuthenticatedUserDTO(response.jwt()));
    }

    @Operation(summary = "Authenticate a user", description = "Authenticate a user")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticatedUserDTO> authenticate(@RequestBody AuthenticationUserDTO request) {

        AuthenticateUserResponse response = mediator.dispatch(new AuthenticateUserRequest(request.getEmail(), request.getPassword()));

        return ResponseEntity.ok(new AuthenticatedUserDTO(response.jwt()));
    }

}


