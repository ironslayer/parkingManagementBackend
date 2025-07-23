package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

/**
 * DTO para actualizar una configuración de tarifa (actualización parcial)
 * Todos los campos son opcionales para permitir actualizaciones parciales
 * 
 * Las validaciones se aplican solo si el campo está presente (no nulo)
 */
public class RateConfigUpdateDTO {
    
    @DecimalMin(value = "0.01", message = "Rate per hour must be greater than 0")
    @JsonProperty("ratePerHour")
    private BigDecimal ratePerHour;
    
    @Min(value = 1, message = "Minimum charge hours must be at least 1")
    @JsonProperty("minimumChargeHours")
    private Integer minimumChargeHours;
    
    @DecimalMin(value = "0.01", message = "Maximum daily rate must be greater than 0 if specified")
    @JsonProperty("maximumDailyRate")
    private BigDecimal maximumDailyRate;
    
    @JsonProperty("isActive")
    private Boolean isActive;

    // Constructor vacío
    public RateConfigUpdateDTO() {}

    // Constructor completo
    public RateConfigUpdateDTO(BigDecimal ratePerHour, Integer minimumChargeHours, 
                             BigDecimal maximumDailyRate, Boolean isActive) {
        this.ratePerHour = ratePerHour;
        this.minimumChargeHours = minimumChargeHours;
        this.maximumDailyRate = maximumDailyRate;
        this.isActive = isActive;
    }

    // Getters y Setters
    public BigDecimal getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(BigDecimal ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public Integer getMinimumChargeHours() {
        return minimumChargeHours;
    }

    public void setMinimumChargeHours(Integer minimumChargeHours) {
        this.minimumChargeHours = minimumChargeHours;
    }

    public BigDecimal getMaximumDailyRate() {
        return maximumDailyRate;
    }

    public void setMaximumDailyRate(BigDecimal maximumDailyRate) {
        this.maximumDailyRate = maximumDailyRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "RateConfigUpdateDTO{" +
                "ratePerHour=" + ratePerHour +
                ", minimumChargeHours=" + minimumChargeHours +
                ", maximumDailyRate=" + maximumDailyRate +
                ", isActive=" + isActive +
                '}';
    }
}
