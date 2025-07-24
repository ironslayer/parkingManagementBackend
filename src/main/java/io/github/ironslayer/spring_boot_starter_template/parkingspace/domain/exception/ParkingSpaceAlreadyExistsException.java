package io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception;

/**
 * Exception thrown when attempting to create a parking space with a space number that already exists.
 */
public class ParkingSpaceAlreadyExistsException extends RuntimeException {
    
    public ParkingSpaceAlreadyExistsException(String spaceNumber) {
        super("Parking space already exists with space number: " + spaceNumber);
    }
}
