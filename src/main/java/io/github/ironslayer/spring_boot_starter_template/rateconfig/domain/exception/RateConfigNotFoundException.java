package io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.exception;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;

/**
 * Excepción lanzada cuando no se encuentra una configuración de tarifa
 */
public class RateConfigNotFoundException extends BadRequestException {

    private static final String DESCRIPTION = "Rate configuration not found";

    public RateConfigNotFoundException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }

    public RateConfigNotFoundException(Long id) {
        super(DESCRIPTION + " with id: " + id);
    }
}
