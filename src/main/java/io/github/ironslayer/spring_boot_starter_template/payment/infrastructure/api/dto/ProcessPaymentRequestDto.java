package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para el request de procesamiento de pago
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentRequestDto {
    
    @JsonProperty("parking_session_id")
    @NotNull(message = "Parking session ID is required")
    @Positive(message = "Parking session ID must be positive")
    private Long parkingSessionId;
    
    @JsonProperty("payment_method")
    @NotNull(message = "Payment method is required")
    private String paymentMethod;
}
