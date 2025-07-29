package io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response para el cálculo de monto a pagar
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateAmountResponse {
    
    /**
     * Monto total calculado
     */
    private BigDecimal totalAmount;
    
    /**
     * Horas estacionado (con decimales)
     */
    private BigDecimal hoursParked;
    
    /**
     * Tarifa por hora aplicada
     */
    private BigDecimal ratePerHour;
    
    /**
     * Mínimo de horas que se cobra
     */
    private Integer minimumChargeHours;
    
    /**
     * Tarifa máxima diaria aplicada (si aplica)
     */
    private BigDecimal maximumDailyRate;
    
    /**
     * ID de la sesión para la cual se calculó
     */
    private Long parkingSessionId;
}
