package io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception;

/**
 * Excepción cuando no se encuentra una sesión activa para un vehículo
 */
public class NoActiveSessionException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "No active parking session found";
    
    public NoActiveSessionException(String licensePlate) {
        super(String.format("No active parking session found for vehicle with license plate '%s'", licensePlate));
    }
    
    public NoActiveSessionException(Long vehicleId) {
        super(String.format("No active parking session found for vehicle with ID '%d'", vehicleId));
    }
    
    public NoActiveSessionException() {
        super(DEFAULT_MESSAGE);
    }
}
