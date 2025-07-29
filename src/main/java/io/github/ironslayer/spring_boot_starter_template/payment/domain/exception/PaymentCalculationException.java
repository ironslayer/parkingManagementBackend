package io.github.ironslayer.spring_boot_starter_template.payment.domain.exception;

/**
 * Excepción lanzada cuando ocurre un error en el cálculo del monto a pagar
 */
public class PaymentCalculationException extends RuntimeException {
    
    public PaymentCalculationException(String message) {
        super(message);
    }
    
    public PaymentCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
