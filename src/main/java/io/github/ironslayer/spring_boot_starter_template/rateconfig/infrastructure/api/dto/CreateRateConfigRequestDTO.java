package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para crear una nueva configuración de tarifa
 * 
 * Solo incluye los campos esenciales:
 * - vehicleTypeId: requerido y positivo
 * - ratePerHour: requerido y mayor a 0
 * - minimumChargeHours: opcional, por defecto 1 hora
 * - maximumDailyRate: opcional
 * 
 * NOTA: isActive no se incluye ya que siempre será true en creación
 */
public class CreateRateConfigRequestDTO {
    
    @NotNull(message = "Vehicle type ID is required")
    @Min(value = 1, message = "Vehicle type ID must be positive")
    @JsonProperty("vehicleTypeId")
    private Long vehicleTypeId;
    
    @NotNull(message = "Rate per hour is required")
    @DecimalMin(value = "0.01", message = "Rate per hour must be greater than 0")
    @JsonProperty("ratePerHour")
    private BigDecimal ratePerHour;
    
    @Min(value = 1, message = "Minimum charge hours must be at least 1")
    @JsonProperty("minimumChargeHours")
    private Integer minimumChargeHours; // Opcional, por defecto será 1
    
    @DecimalMin(value = "0.01", message = "Maximum daily rate must be greater than 0 if specified")
    @JsonProperty("maximumDailyRate")
    private BigDecimal maximumDailyRate;
    
    // NOTA: isActive NO está incluido - siempre será true en creación

    // Constructor vacío
    public CreateRateConfigRequestDTO() {}

    // Constructor sin isActive (siempre será true)
    public CreateRateConfigRequestDTO(Long vehicleTypeId, BigDecimal ratePerHour, 
                                    Integer minimumChargeHours, BigDecimal maximumDailyRate) {
        this.vehicleTypeId = vehicleTypeId;
        this.ratePerHour = ratePerHour;
        this.minimumChargeHours = minimumChargeHours != null ? minimumChargeHours : 1;
        this.maximumDailyRate = maximumDailyRate;
    }

    // Getters y Setters
    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public BigDecimal getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(BigDecimal ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public Integer getMinimumChargeHours() {
        return minimumChargeHours != null ? minimumChargeHours : 1;
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

    @Override
    public String toString() {
        return "CreateRateConfigRequestDTO{" +
                "vehicleTypeId=" + vehicleTypeId +
                ", ratePerHour=" + ratePerHour +
                ", minimumChargeHours=" + getMinimumChargeHours() +
                ", maximumDailyRate=" + maximumDailyRate +
                '}';
    }
}
