package io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response para el comando de procesar pago
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentResponse {
    
    /**
     * Pago procesado
     */
    private Payment payment;
    
    /**
     * Mensaje descriptivo del resultado
     */
    private String message;
    
    public static ProcessPaymentResponse success(Payment payment) {
        return new ProcessPaymentResponse(payment, "Payment processed successfully");
    }
}
