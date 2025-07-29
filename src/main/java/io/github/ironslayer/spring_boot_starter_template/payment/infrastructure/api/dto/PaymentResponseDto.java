package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta de consulta de pago
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("parking_session_id")
    private Long parkingSessionId;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("paid_at")
    private LocalDateTime paidAt;
}
