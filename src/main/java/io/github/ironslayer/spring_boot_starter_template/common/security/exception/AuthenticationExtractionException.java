package io.github.ironslayer.spring_boot_starter_template.common.security.exception;

/**
 * Exception lanzada cuando no se puede extraer información del usuario autenticado
 */
public class AuthenticationExtractionException extends RuntimeException {
    
    public AuthenticationExtractionException(String message) {
        super(message);
    }
    
    public AuthenticationExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}



