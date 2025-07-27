package io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception;

/**
 * Excepción cuando un vehículo ya tiene una sesión activa en el parqueadero
 */
public class VehicleAlreadyParkedException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Vehicle is already parked";
    
    public VehicleAlreadyParkedException(String licensePlate) {
        super(String.format("Vehicle with license plate '%s' already has an active parking session", licensePlate));
    }
    
    public VehicleAlreadyParkedException(Long vehicleId) {
        super(String.format("Vehicle with ID '%d' already has an active parking session", vehicleId));
    }
    
    public VehicleAlreadyParkedException() {
        super(DEFAULT_MESSAGE);
    }
}
