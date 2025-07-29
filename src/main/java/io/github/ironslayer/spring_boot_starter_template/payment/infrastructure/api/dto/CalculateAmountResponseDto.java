package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la respuesta de cálculo de monto de pago
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateAmountResponseDto {
    
    @JsonProperty("parking_session_id")
    private Long parkingSessionId;
    
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    
    @JsonProperty("hours_parked")
    private BigDecimal hoursParked;
    
    @JsonProperty("rate_applied")
    private BigDecimal rateApplied;
}
