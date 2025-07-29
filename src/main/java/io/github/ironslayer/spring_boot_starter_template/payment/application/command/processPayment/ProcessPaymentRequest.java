package io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request para procesar un pago
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentRequest implements Request<ProcessPaymentResponse> {
    
    /**
     * ID de la sesión de parqueo a pagar
     */
    private Long parkingSessionId;
    
    /**
     * Método de pago elegido
     */
    private PaymentMethod paymentMethod;
    
    /**
     * ID del operador que procesa el pago
     */
    private Long operatorId;
    
    /**
     * Monto calculado (opcional - se calculará automáticamente si no se proporciona)
     */
    private BigDecimal totalAmount;
}
