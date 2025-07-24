package io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception;

/**
 * Exception thrown when attempting to operate on a parking space that is not available.
 * This includes spaces that are occupied, inactive, or otherwise unavailable.
 */
public class ParkingSpaceNotAvailableException extends RuntimeException {
    
    public ParkingSpaceNotAvailableException(String message) {
        super(message);
    }
    
    public ParkingSpaceNotAvailableException(String spaceNumber, String reason) {
        super("Parking space " + spaceNumber + " is not available: " + reason);
    }
}
