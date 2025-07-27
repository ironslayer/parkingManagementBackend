package io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception;

/**
 * Excepción cuando no hay espacios disponibles para un tipo de vehículo
 */
public class NoAvailableSpaceException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "No available parking spaces";
    
    public NoAvailableSpaceException(String vehicleType) {
        super(String.format("No available parking spaces for vehicle type '%s'", vehicleType));
    }
    
    public NoAvailableSpaceException() {
        super(DEFAULT_MESSAGE);
    }
}
