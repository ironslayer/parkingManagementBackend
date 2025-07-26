package io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception;

/**
 * Excepción que se lanza cuando se intenta registrar un vehículo con una placa que ya existe.
 */
public class VehicleLicensePlateAlreadyExistsException extends RuntimeException {
    
    public VehicleLicensePlateAlreadyExistsException(String licensePlate) {
        super("Vehicle with license plate '" + licensePlate + "' already exists");
    }
}
