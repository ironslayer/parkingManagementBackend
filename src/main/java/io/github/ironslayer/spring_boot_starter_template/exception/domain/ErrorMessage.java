package io.github.ironslayer.spring_boot_starter_template.exception.domain;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorMessage {
    private String exception;
    private String message;
    private String path;

    public ErrorMessage(Exception exception, String path) {
        this.exception = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.path = path;
    }

    public ErrorMessage(Exception exception, String path, Map<String, String> errors) {
        this.exception = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.path = path;
    }


}
