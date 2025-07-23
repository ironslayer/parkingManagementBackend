package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para las configuraciones de tarifas
 * 
 * Incluye toda la información de una configuración para mostrar
 * en las respuestas de las APIs
 */
public class RateConfigResponseDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("vehicleTypeId")
    private Long vehicleTypeId;
    
    @JsonProperty("ratePerHour")
    private BigDecimal ratePerHour;
    
    @JsonProperty("minimumChargeHours")
    private Integer minimumChargeHours;
    
    @JsonProperty("maximumDailyRate")
    private BigDecimal maximumDailyRate;
    
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Constructor vacío
    public RateConfigResponseDTO() {}

    // Constructor completo
    public RateConfigResponseDTO(Long id, Long vehicleTypeId, BigDecimal ratePerHour, 
                               Integer minimumChargeHours, BigDecimal maximumDailyRate,
                               Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.vehicleTypeId = vehicleTypeId;
        this.ratePerHour = ratePerHour;
        this.minimumChargeHours = minimumChargeHours;
        this.maximumDailyRate = maximumDailyRate;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "RateConfigResponseDTO{" +
                "id=" + id +
                ", vehicleTypeId=" + vehicleTypeId +
                ", ratePerHour=" + ratePerHour +
                ", minimumChargeHours=" + minimumChargeHours +
                ", maximumDailyRate=" + maximumDailyRate +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
