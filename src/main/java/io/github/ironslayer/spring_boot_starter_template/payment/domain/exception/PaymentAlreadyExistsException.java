package io.github.ironslayer.spring_boot_starter_template.payment.domain.exception;

/**
 * Excepción lanzada cuando ya existe un pago para una sesión de parqueo
 */
public class PaymentAlreadyExistsException extends RuntimeException {
    
    public PaymentAlreadyExistsException(String message) {
        super(message);
    }
    
    public PaymentAlreadyExistsException(Long parkingSessionId) {
        super("Payment already exists for parking session with ID: " + parkingSessionId);
    }
}
