package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api;


import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser.AuthenticateUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.authenticateUser.AuthenticateUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.registerOperator.RegisterOperatorRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.registerOperator.RegisterOperatorResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.updateProfile.UpdateProfileRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUser.UpdateUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUserPartial.UpdateUserPartialRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUserStatus.UpdateUserStatusRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers.GetAllUsersRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers.GetAllUsersResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser.GetUserRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser.GetUserResponse;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getCurrentUserProfile.GetCurrentUserProfileRequest;
import io.github.ironslayer.spring_boot_starter_template.user.application.query.getCurrentUserProfile.GetCurrentUserProfileResponse;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticatedUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticationUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.RegisterUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UpdateProfileRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UpdateUserRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UpdateUserStatusRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Find a user", description = "Find a user (ADMIN only)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {

        GetUserResponse response = mediator.dispatch(new GetUserRequest(id));

        UserDTO userDTO = userMapper.userToUserDTO(response.user());

        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "List all users", description = "List all users (ADMIN only)")
    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDTO>> findAll() {

        log.info("UserController Get all users");

        GetAllUsersResponse response = mediator.dispatch(new GetAllUsersRequest());

        List<UserDTO> userDTOS = response.users().stream().map(userMapper::userToUserDTO).toList();

        return ResponseEntity.ok(userDTOS);
    }

    @Operation(summary = "Update a user", description = "Update a user (ADMIN only)")
    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> update(@RequestBody UserDTO userDTO) {

        User user = userMapper.userDTOToUser(userDTO);

        mediator.dispatch(new UpdateUserRequest(user));

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user partially", description = "Partially update a user (ADMIN only)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateUserPartial(
            @PathVariable Long id, 
            @RequestBody @Valid UpdateUserRequestDTO request) {
        
        log.info("Updating user partially: userId={}, fields={}", id, request);
        
        UpdateUserPartialRequest partialRequest = new UpdateUserPartialRequest(
            id, 
            request.firstname(), 
            request.lastname(), 
            request.email(), 
            request.role()
        );
        
        mediator.dispatch(partialRequest);
        
        log.info("Successfully updated user partially: userId={}", id);
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user status", description = "Change user status (activate/deactivate) - ADMIN only")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long id, 
            @RequestBody @Valid UpdateUserStatusRequestDTO request) {
        
        log.info("Updating user status: userId={}, newStatus={}", id, request.isActive());
        
        UpdateUserStatusRequest statusRequest = new UpdateUserStatusRequest(id, request.isActive());
        mediator.dispatch(statusRequest);
        
        log.info("Successfully updated user status: userId={}", id);
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user profile", description = "Update the profile of the authenticated user")
    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProfile(@RequestBody @Valid UpdateProfileRequestDTO request) {
        
        log.info("Updating user profile for authenticated user");
        
        UpdateProfileRequest profileRequest = new UpdateProfileRequest(
            request.firstname(),
            request.lastname(),
            request.currentPassword(),
            request.newPassword()
        );
        
        mediator.dispatch(profileRequest);
        
        log.info("Successfully updated user profile");
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Register an operator", description = "Register a new operator for the parking system (ADMIN only)")
    @PostMapping("/register-operator")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthenticatedUserDTO> registerOperator(@RequestBody @Validated RegisterUserDTO request) {

        User user = userMapper.registerUserDTOToUser(request);

        RegisterOperatorResponse response = mediator.dispatch(new RegisterOperatorRequest(user));

        return ResponseEntity.ok(new AuthenticatedUserDTO(response.jwt()));
    }

    @Operation(summary = "Authenticate a user", description = "Authenticate a user")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticatedUserDTO> authenticate(@RequestBody AuthenticationUserDTO request) {

        AuthenticateUserResponse response = mediator.dispatch(new AuthenticateUserRequest(request.getEmail(), request.getPassword()));

        return ResponseEntity.ok(new AuthenticatedUserDTO(response.jwt()));
    }

    @Operation(summary = "Logout a user", description = "Logout the authenticated user")
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout() {
        // En JWT stateless, el logout es manejado del lado del cliente
        // eliminando el token. Aquí simplemente confirmamos que el token es válido
        log.info("User logout successful");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        
        log.info("Getting current user profile");
        
        GetCurrentUserProfileResponse response = mediator.dispatch(new GetCurrentUserProfileRequest());
        
        UserDTO userDTO = userMapper.userToUserDTO(response.user());
        
        log.info("Successfully retrieved profile for user: {}", userDTO.getEmail());
        
        return ResponseEntity.ok(userDTO);
    }

}


