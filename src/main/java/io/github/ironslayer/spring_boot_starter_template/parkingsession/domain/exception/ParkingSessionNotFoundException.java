package io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception;

/**
 * Excepción cuando no se encuentra una sesión de parqueo específica
 */
public class ParkingSessionNotFoundException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Parking session not found";
    
    public ParkingSessionNotFoundException(Long sessionId) {
        super(String.format("Parking session with ID '%d' not found", sessionId));
    }
    
    public ParkingSessionNotFoundException(String ticketCode) {
        super(String.format("Parking session with ticket code '%s' not found", ticketCode));
    }
    
    public ParkingSessionNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
