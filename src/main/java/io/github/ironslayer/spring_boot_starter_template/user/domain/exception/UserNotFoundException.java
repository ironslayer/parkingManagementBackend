package io.github.ironslayer.spring_boot_starter_template.user.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
