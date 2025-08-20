package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateProfile;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.common.security.AuthenticatedUserService;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserStatusConflictException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para actualizar el perfil personal del usuario autenticado.
 * Permite actualización de nombre, apellido y contraseña.
 * 
 * Validaciones de negocio implementadas:
 * - Usuario debe existir y estar autenticado
 * - Si cambia contraseña, debe proporcionar la actual
 * - Verificar contraseña actual antes de cambiar
 * - Solo campos no-null son actualizados
 * 
 * ACCESIBLE para cualquier usuario autenticado
 */
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateProfileHandler implements RequestHandler<UpdateProfileRequest, Void> {
    
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Void handle(UpdateProfileRequest request) {
        log.info("Handling profile update for authenticated user");
        
        // Obtener usuario autenticado
        Long currentUserId = authenticatedUserService.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found with ID: " + currentUserId));
        
        log.info("Updating profile for user: {} ({})", user.getEmail(), user.getRole());
        
        // Validar entrada
        validateRequest(request);
        
        // Aplicar cambios parciales
        boolean hasChanges = false;
        
        // Actualizar firstname si se proporciona
        if (request.firstname() != null && !request.firstname().trim().isEmpty()) {
            if (!request.firstname().equals(user.getFirstname())) {
                user.setFirstname(request.firstname().trim());
                hasChanges = true;
                log.info("Updated firstname for user {}", user.getEmail());
            }
        }
        
        // Actualizar lastname si se proporciona
        if (request.lastname() != null && !request.lastname().trim().isEmpty()) {
            if (!request.lastname().equals(user.getLastname())) {
                user.setLastname(request.lastname().trim());
                hasChanges = true;
                log.info("Updated lastname for user {}", user.getEmail());
            }
        }
        
        // Actualizar contraseña si se proporciona
        if (request.newPassword() != null && !request.newPassword().trim().isEmpty()) {
            validatePasswordChange(request, user);
            String encodedPassword = passwordEncoder.encode(request.newPassword());
            user.setPassword(encodedPassword);
            hasChanges = true;
            log.info("Updated password for user {}", user.getEmail());
        }
        
        // Solo guardar si hay cambios
        if (hasChanges) {
            user.setUpdatedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            log.info("Successfully updated profile for user {}", user.getEmail());
        } else {
            log.info("No changes detected for user profile {}", user.getEmail());
        }
        
        return null;
    }
    
    @Override
    public Class<UpdateProfileRequest> getRequestType() {
        return UpdateProfileRequest.class;
    }
    
    private void validateRequest(UpdateProfileRequest request) {
        // Verificar que al menos un campo esté presente para actualizar
        if (request.firstname() == null && request.lastname() == null && request.newPassword() == null) {
            throw new BadRequestException("At least one field must be provided for profile update");
        }
        
        // Si hay nueva contraseña, debe haber contraseña actual
        if (request.newPassword() != null && !request.newPassword().trim().isEmpty()) {
            if (request.currentPassword() == null || request.currentPassword().trim().isEmpty()) {
                throw new BadRequestException("Current password is required when changing password");
            }
        }
    }
    
    private void validatePasswordChange(UpdateProfileRequest request, User user) {
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UserStatusConflictException("Current password is incorrect");
        }
        
        // Verificar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }
    }
}
