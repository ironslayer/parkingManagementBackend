package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUserStatus;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserStatusConflictException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para cambiar el estado (activo/inactivo) de un usuario.
 * Implementa soft delete siguiendo el mismo patrón que el módulo de vehículos.
 * 
 * Validaciones de negocio implementadas:
 * - No permitir desactivar el último usuario ADMIN del sistema
 * - Validar que el usuario no se desactive a sí mismo
 * - Verificar que el usuario existe
 * - Evitar cambios redundantes (cambiar al mismo estado)
 * 
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 */
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateUserStatusHandler implements RequestHandler<UpdateUserStatusRequest, Void> {
    
    private final UserRepository userRepository;
    
    @Override
    public Void handle(UpdateUserStatusRequest request) {
        log.info("Handling user status update: userId={}, newStatus={}", request.userId(), request.isActive());
        
        // Validar entrada
        validateRequest(request);
        
        // Buscar usuario
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + request.userId() + " not found"));
        
        log.info("Found user: email={}, currentStatus={}, role={}", user.getEmail(), user.getIsActive(), user.getRole());
        
        // Verificar que no esté ya en el estado solicitado
        boolean currentStatus = Boolean.TRUE.equals(user.getIsActive());
        if (currentStatus == request.isActive()) {
            String action = request.isActive() ? "active" : "inactive";
            throw new BadRequestException("User with ID " + request.userId() + " is already " + action);
        }
        
        // Validaciones específicas para desactivación
        if (!request.isActive()) {
            validateDeactivation(user);
        }
        
        // Actualizar estado usando lógica de dominio
        updateUserStatus(user, request.isActive());
        
        // Guardar cambios
        User savedUser = userRepository.save(user);
        
        String action = request.isActive() ? "activated" : "deactivated";
        log.info("Successfully {} user: userId={}, email={}", action, savedUser.getId(), savedUser.getEmail());
        
        return null;
    }
    
    @Override
    public Class<UpdateUserStatusRequest> getRequestType() {
        return UpdateUserStatusRequest.class;
    }
    
    private void validateRequest(UpdateUserStatusRequest request) {
        if (request.userId() == null) {
            throw new BadRequestException("User ID cannot be null");
        }
        
        if (request.userId() <= 0) {
            throw new BadRequestException("User ID must be positive");
        }
        
        if (request.isActive() == null) {
            throw new BadRequestException("isActive status cannot be null");
        }
    }
    
    private void validateDeactivation(User user) {
        // Validar que no es el último ADMIN
        if (Role.ADMIN.equals(user.getRole())) {
            long activeAdminCount = userRepository.findAll()
                    .stream()
                    .filter(u -> Role.ADMIN.equals(u.getRole()))
                    .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                    .count();
            
            if (activeAdminCount <= 1) {
                throw new UserStatusConflictException("Cannot deactivate the last active ADMIN user in the system");
            }
        }
        
        // Validar que el usuario no se desactive a sí mismo
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            if (authentication.getName().equals(user.getEmail())) {
                throw new UserStatusConflictException("Users cannot deactivate themselves");
            }
        }
    }
    
    private void updateUserStatus(User user, Boolean isActive) {
        // Usar métodos de dominio si existen, o actualizar directamente
        user.setIsActive(isActive);
        user.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
