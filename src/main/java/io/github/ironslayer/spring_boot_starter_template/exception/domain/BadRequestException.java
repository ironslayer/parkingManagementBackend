package io.github.ironslayer.spring_boot_starter_template.exception.domain;

public class BadRequestException extends RuntimeException {

    private static final String DESCRIPTION = "Bad Request Exception (400)";

    public BadRequestException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
}
