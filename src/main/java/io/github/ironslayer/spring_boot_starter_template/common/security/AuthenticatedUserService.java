package io.github.ironslayer.spring_boot_starter_template.common.security;

import io.github.ironslayer.spring_boot_starter_template.config.application.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Servicio para obtener información del usuario autenticado desde el contexto de seguridad
 * Extrae datos del JWT de forma eficiente sin consultas adicionales a la base de datos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedUserService {
    
    private final JwtService jwtService;
    
    /**
     * Obtiene el ID del usuario autenticado desde el JWT
     * 
     * @return ID del usuario autenticado
     * @throws SecurityException si no hay usuario autenticado o no se puede extraer el ID
     */
    public Long getCurrentUserId() {
        try {
            String jwt = extractJwtFromRequest();
            if (jwt == null) {
                throw new SecurityException("No JWT token found in request");
            }
            
            Long userId = jwtService.extractUserId(jwt);
            if (userId == null) {
                throw new SecurityException("User ID not found in JWT token");
            }
            
            log.debug("Extracted user ID: {} from JWT", userId);
            return userId;
            
        } catch (Exception e) {
            log.error("Error extracting user ID from security context: {}", e.getMessage());
            throw new SecurityException("Could not extract authenticated user ID", e);
        }
    }
    
    /**
     * Obtiene el email (username) del usuario autenticado desde el SecurityContext
     * 
     * @return Email del usuario autenticado
     * @throws SecurityException si no hay usuario autenticado
     */
    public String getCurrentUserEmail() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new SecurityException("No authenticated user found");
            }
            
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String email = ((UserDetails) principal).getUsername();
                log.debug("Extracted user email: {} from SecurityContext", email);
                return email;
            }
            
            throw new SecurityException("Invalid principal type in security context");
            
        } catch (Exception e) {
            log.error("Error extracting user email from security context: {}", e.getMessage());
            throw new SecurityException("Could not extract authenticated user email", e);
        }
    }
    
    /**
     * Verifica si el usuario actual tiene un rol específico
     * 
     * @param role El rol a verificar (ej: "ADMIN", "OPERATOR")
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(role));
                    
        } catch (Exception e) {
            log.error("Error checking user role: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrae el token JWT de la request HTTP actual
     * 
     * @return JWT token sin el prefijo "Bearer ", o null si no se encuentra
     */
    private String extractJwtFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("Error extracting JWT from request: {}", e.getMessage());
            return null;
        }
    }
}
