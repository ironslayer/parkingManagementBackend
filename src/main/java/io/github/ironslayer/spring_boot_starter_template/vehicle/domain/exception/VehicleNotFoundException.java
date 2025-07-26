package io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception;

/**
 * Excepción que se lanza cuando no se encuentra un vehículo.
 */
public class VehicleNotFoundException extends RuntimeException {
    
    public VehicleNotFoundException(Long id) {
        super("Vehicle not found with ID: " + id);
    }
    
    public static VehicleNotFoundException forLicensePlate(String licensePlate) {
        return new VehicleNotFoundException("Vehicle not found with license plate: " + licensePlate);
    }
    
    public VehicleNotFoundException(String message) {
        super(message);
    }
}
