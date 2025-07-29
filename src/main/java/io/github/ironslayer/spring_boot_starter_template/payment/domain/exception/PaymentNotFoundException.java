package io.github.ironslayer.spring_boot_starter_template.payment.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un pago específico
 */
public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException(String message) {
        super(message);
    }
    
    public PaymentNotFoundException(Long id) {
        super("Payment not found with ID: " + id);
    }
    
    public PaymentNotFoundException(String field, Object value) {
        super("Payment not found with " + field + ": " + value);
    }
}
