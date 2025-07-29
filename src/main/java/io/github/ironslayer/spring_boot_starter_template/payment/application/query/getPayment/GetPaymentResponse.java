package io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response con la información del pago
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPaymentResponse {
    
    /**
     * ID del pago
     */
    private Long id;
    
    /**
     * ID de la sesión de parqueo
     */
    private Long parkingSessionId;
    
    /**
     * Método de pago
     */
    private String paymentMethod;
    
    /**
     * Estado del pago
     */
    private String status;
    
    /**
     * Monto del pago
     */
    private BigDecimal amount;
    
    /**
     * Fecha de creación del pago
     */
    private LocalDateTime createdAt;
    
    /**
     * Fecha de pago realizado
     */
    private LocalDateTime paidAt;
}
