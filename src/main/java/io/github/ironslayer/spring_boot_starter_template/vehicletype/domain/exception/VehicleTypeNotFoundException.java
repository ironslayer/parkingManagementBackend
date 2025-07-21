package io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;

/**
 * Excepción lanzada cuando no se encuentra un tipo de vehículo
 */
public class VehicleTypeNotFoundException extends BadRequestException {

    private static final String DESCRIPTION = "Vehicle type not found";

    public VehicleTypeNotFoundException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }

    public VehicleTypeNotFoundException(Long id) {
        super(DESCRIPTION + " with id: " + id);
    }
}
