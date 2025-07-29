package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta de procesamiento de pago
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentResponseDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("parking_session_id")
    private Long parkingSessionId;
    
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    
    @JsonProperty("hours_parked")
    private BigDecimal hoursParked;
    
    @JsonProperty("rate_applied")
    private BigDecimal rateApplied;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("payment_status")
    private String paymentStatus;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("paid_at")
    private LocalDateTime paidAt;
}
