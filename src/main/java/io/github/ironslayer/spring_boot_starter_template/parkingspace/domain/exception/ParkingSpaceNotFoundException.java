package io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception;

/**
 * Exception thrown when a parking space is not found.
 */
public class ParkingSpaceNotFoundException extends RuntimeException {
    
    public ParkingSpaceNotFoundException(String message) {
        super(message);
    }
    
    public ParkingSpaceNotFoundException(Long id) {
        super("Parking space not found with ID: " + id);
    }
    
    public ParkingSpaceNotFoundException(String spaceNumber, boolean bySpaceNumber) {
        super("Parking space not found with space number: " + spaceNumber);
    }
}
