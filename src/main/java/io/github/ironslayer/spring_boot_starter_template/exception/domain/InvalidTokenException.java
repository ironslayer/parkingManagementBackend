package io.github.ironslayer.spring_boot_starter_template.exception.domain;

public class InvalidTokenException extends BadRequestException {

    private static final String DESCRIPTION = "Token expired";

    public InvalidTokenException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
}
