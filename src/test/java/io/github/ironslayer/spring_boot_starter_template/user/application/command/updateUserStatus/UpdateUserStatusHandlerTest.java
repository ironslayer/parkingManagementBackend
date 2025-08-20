package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUserStatus;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserStatusConflictException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserStatusHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UpdateUserStatusHandler handler;

    @Test
    void handle_DeactivateUser_Success() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, false);
        User user = createActiveOperator(1L, "operator@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");

        // Act
        Void result = handler.handle(request);

        // Assert
        assertNull(result);
        verify(userRepository).save(any(User.class));
        assertFalse(user.getIsActive());
    }

    @Test
    void handle_ActivateUser_Success() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, true);
        User user = createInactiveOperator(1L, "operator@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Void result = handler.handle(request);

        // Assert
        assertNull(result);
        verify(userRepository).save(any(User.class));
        assertTrue(user.getIsActive());
    }

    @Test
    void handle_UserNotFound_ThrowsException() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> handler.handle(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void handle_AlreadyActive_ThrowsException() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, true);
        User user = createActiveOperator(1L, "operator@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> handler.handle(request));
        assertEquals("User with ID 1 is already active", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void handle_AlreadyInactive_ThrowsException() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, false);
        User user = createInactiveOperator(1L, "operator@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> handler.handle(request));
        assertEquals("User with ID 1 is already inactive", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void handle_DeactivateLastAdmin_ThrowsException() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, false);
        User admin = createActiveAdmin(1L, "admin@test.com");
        User inactiveAdmin = createInactiveAdmin(2L, "admin2@test.com");
        User operator = createActiveOperator(3L, "operator@test.com");
        
        List<User> allUsers = Arrays.asList(admin, inactiveAdmin, operator);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findAll()).thenReturn(allUsers);

        // Act & Assert
        UserStatusConflictException exception = assertThrows(UserStatusConflictException.class, 
            () -> handler.handle(request));
        assertEquals("Cannot deactivate the last active ADMIN user in the system", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void handle_DeactivateOneOfMultipleAdmins_Success() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, false);
        User admin1 = createActiveAdmin(1L, "admin1@test.com");
        User admin2 = createActiveAdmin(2L, "admin2@test.com");
        User operator = createActiveOperator(3L, "operator@test.com");
        
        List<User> allUsers = Arrays.asList(admin1, admin2, operator);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin1));
        when(userRepository.findAll()).thenReturn(allUsers);
        when(userRepository.save(any(User.class))).thenReturn(admin1);
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin2@test.com");

        // Act
        Void result = handler.handle(request);

        // Assert
        assertNull(result);
        verify(userRepository).save(any(User.class));
        assertFalse(admin1.getIsActive());
    }

    @Test
    void handle_UserDeactivatesSelf_ThrowsException() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(1L, false);
        User user = createActiveOperator(1L, "operator@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("operator@test.com");

        // Act & Assert
        UserStatusConflictException exception = assertThrows(UserStatusConflictException.class, 
            () -> handler.handle(request));
        assertEquals("Users cannot deactivate themselves", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void handle_NullUserId_ThrowsException() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(null, false);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> handler.handle(request));
        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    void handle_NegativeUserId_ThrowsException() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(-1L, false);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> handler.handle(request));
        assertEquals("User ID must be positive", exception.getMessage());
    }

    @Test
    void getRequestType_ReturnsCorrectType() {
        // Act & Assert
        assertEquals(UpdateUserStatusRequest.class, handler.getRequestType());
    }

    // Helper methods
    private User createActiveOperator(Long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .firstname("Test")
                .lastname("Operator")
                .role(Role.OPERATOR)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User createInactiveOperator(Long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .firstname("Test")
                .lastname("Operator")
                .role(Role.OPERATOR)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User createActiveAdmin(Long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .firstname("Test")
                .lastname("Admin")
                .role(Role.ADMIN)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User createInactiveAdmin(Long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .firstname("Test")
                .lastname("Admin")
                .role(Role.ADMIN)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
