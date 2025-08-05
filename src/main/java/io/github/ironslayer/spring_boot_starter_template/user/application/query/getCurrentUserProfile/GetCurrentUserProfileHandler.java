package io.github.ironslayer.spring_boot_starter_template.user.application.query.getCurrentUserProfile;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.common.security.AuthenticatedUserService;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler para obtener el perfil del usuario autenticado actualmente.
 * Utiliza el JWT token para identificar al usuario sin necesidad de pasar el ID.
 * ACCESIBLE para cualquier usuario autenticado (ADMIN, OPERATOR, CUSTOMER).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetCurrentUserProfileHandler implements RequestHandler<GetCurrentUserProfileRequest, GetCurrentUserProfileResponse> {

    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public GetCurrentUserProfileResponse handle(GetCurrentUserProfileRequest request) {
        
        log.info("Getting current user profile from authenticated context");
        
        // Obtener el ID del usuario desde el JWT token
        Long currentUserId = authenticatedUserService.getCurrentUserId();
        
        log.debug("Retrieved user ID: {} from JWT token", currentUserId);
        
        // Buscar el usuario en la base de datos
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found in database", currentUserId);
                    return new UserNotFoundException("User not found with ID: " + currentUserId);
                });
        
        log.info("Successfully retrieved profile for user: {} ({})", user.getEmail(), user.getRole());
        
        return new GetCurrentUserProfileResponse(user);
    }

    @Override
    public Class<GetCurrentUserProfileRequest> getRequestType() {
        return GetCurrentUserProfileRequest.class;
    }
}
