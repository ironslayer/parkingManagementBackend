package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUserPartial;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserStatusConflictException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para actualizar parcialmente un usuario.
 * Implementa actualización parcial siguiendo el patrón del módulo de vehículos.
 * 
 * Validaciones de negocio implementadas:
 * - Validar que el usuario existe
 * - Verificar unicidad de email si se cambia
 * - Validar cambios de rol (no desactivar último ADMIN)
 * - Solo campos no-null son actualizados
 * 
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 */
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateUserPartialHandler implements RequestHandler<UpdateUserPartialRequest, Void> {
    
    private final UserRepository userRepository;
    
    @Override
    public Void handle(UpdateUserPartialRequest request) {
        log.info("Handling partial user update: userId={}", request.userId());
        
        // Validar entrada
        validateRequest(request);
        
        // Buscar usuario
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + request.userId() + " not found"));
        
        log.info("Found user: email={}, role={}", user.getEmail(), user.getRole());
        
        // Aplicar cambios parciales
        boolean hasChanges = false;
        
        // Actualizar firstname si se proporciona
        if (request.firstname() != null && !request.firstname().trim().isEmpty()) {
            if (!request.firstname().equals(user.getFirstname())) {
                user.setFirstname(request.firstname().trim());
                hasChanges = true;
                log.info("Updated firstname for user ID {}", request.userId());
            }
        }
        
        // Actualizar lastname si se proporciona
        if (request.lastname() != null && !request.lastname().trim().isEmpty()) {
            if (!request.lastname().equals(user.getLastname())) {
                user.setLastname(request.lastname().trim());
                hasChanges = true;
                log.info("Updated lastname for user ID {}", request.userId());
            }
        }
        
        // Actualizar email si se proporciona
        if (request.email() != null && !request.email().trim().isEmpty()) {
            if (!request.email().equals(user.getEmail())) {
                validateEmailUniqueness(request.email(), request.userId());
                user.setEmail(request.email().trim());
                hasChanges = true;
                log.info("Updated email for user ID {}", request.userId());
            }
        }
        
        // Actualizar role si se proporciona
        if (request.role() != null && !request.role().trim().isEmpty()) {
            Role newRole = parseRole(request.role());
            if (!newRole.equals(user.getRole())) {
                validateRoleChange(user, newRole);
                user.setRole(newRole);
                hasChanges = true;
                log.info("Updated role for user ID {} from {} to {}", request.userId(), user.getRole(), newRole);
            }
        }
        
        // Solo guardar si hay cambios
        if (hasChanges) {
            user.setUpdatedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            log.info("Successfully updated user ID {} with partial changes", request.userId());
        } else {
            log.info("No changes detected for user ID {}", request.userId());
        }
        
        return null;
    }
    
    @Override
    public Class<UpdateUserPartialRequest> getRequestType() {
        return UpdateUserPartialRequest.class;
    }
    
    private void validateRequest(UpdateUserPartialRequest request) {
        if (request.userId() == null || request.userId() <= 0) {
            throw new BadRequestException("User ID must be positive");
        }
        
        // Verificar que al menos un campo esté presente para actualizar
        if (request.firstname() == null && request.lastname() == null && 
            request.email() == null && request.role() == null) {
            throw new BadRequestException("At least one field must be provided for update");
        }
    }
    
    private void validateEmailUniqueness(String email, Long currentUserId) {
        if (userRepository.existsByEmail(email)) {
            // Verificar si el email pertenece al mismo usuario
            userRepository.findByEmail(email).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUserId)) {
                    throw new UserStatusConflictException("Email " + email + " is already in use by another user");
                }
            });
        }
    }
    
    private Role parseRole(String roleString) {
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + roleString + ". Valid roles are: ADMIN, OPERATOR");
        }
    }
    
    private void validateRoleChange(User user, Role newRole) {
        // Si se está cambiando de ADMIN a OPERATOR, verificar que no sea el último ADMIN
        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            long activeAdminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && Boolean.TRUE.equals(u.getIsActive()))
                    .count();
            
            if (activeAdminCount <= 1) {
                throw new UserStatusConflictException("Cannot change role of the last active ADMIN user in the system");
            }
        }
    }
}
