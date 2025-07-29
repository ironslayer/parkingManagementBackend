package io.github.ironslayer.spring_boot_starter_template.payment.domain.entity;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa un pago realizado por un vehículo estacionado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    private Long id;
    
    /**
     * ID de la sesión de parqueo asociada a este pago
     */
    private Long parkingSessionId;
    
    /**
     * Monto total a pagar
     */
    private BigDecimal totalAmount;
    
    /**
     * Cantidad de horas estacionado (puede incluir decimales)
     */
    private BigDecimal hoursParked;
    
    /**
     * Tarifa por hora aplicada en el momento del cálculo
     */
    private BigDecimal rateApplied;
    
    /**
     * Método de pago utilizado
     */
    private PaymentMethod paymentMethod;
    
    /**
     * Estado actual del pago
     */
    private PaymentStatus paymentStatus;
    
    /**
     * Fecha y hora cuando se realizó el pago efectivo
     * (null si aún está pendiente)
     */
    private LocalDateTime paidAt;
    
    /**
     * ID del operador que procesó el pago
     */
    private Long operatorId;
    
    /**
     * Fecha de creación del registro de pago
     */
    private LocalDateTime createdAt;
    
    /**
     * Constructor para crear un nuevo pago
     */
    public static Payment createNewPayment(
            Long parkingSessionId,
            BigDecimal totalAmount, 
            BigDecimal hoursParked,
            BigDecimal rateApplied,
            PaymentMethod paymentMethod,
            Long operatorId) {
        
        return Payment.builder()
                .parkingSessionId(parkingSessionId)
                .totalAmount(totalAmount)
                .hoursParked(hoursParked)
                .rateApplied(rateApplied)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.PENDING)
                .operatorId(operatorId)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Marcar el pago como completado
     */
    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }
    
    /**
     * Cancelar el pago
     */
    public void cancel() {
        this.paymentStatus = PaymentStatus.CANCELLED;
    }
    
    /**
     * Verificar si el pago está pendiente
     */
    public boolean isPending() {
        return this.paymentStatus == PaymentStatus.PENDING;
    }
    
    /**
     * Verificar si el pago está completado
     */
    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.PAID;
    }
    
    /**
     * Verificar si el pago está cancelado
     */
    public boolean isCancelled() {
        return this.paymentStatus == PaymentStatus.CANCELLED;
    }
}
