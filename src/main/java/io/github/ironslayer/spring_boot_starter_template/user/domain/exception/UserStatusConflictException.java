package io.github.ironslayer.spring_boot_starter_template.user.domain.exception;

/**
 * Excepción que se lanza cuando se produce un conflicto al intentar cambiar el estado de un usuario.
 */
public class UserStatusConflictException extends RuntimeException {
    
    public UserStatusConflictException(String message) {
        super(message);
    }
}
