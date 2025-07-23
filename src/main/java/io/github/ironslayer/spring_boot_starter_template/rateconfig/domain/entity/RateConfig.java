package io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para configuración de tarifas en el sistema de parqueo
 * 
 * Representa las tarifas aplicables a diferentes tipos de vehículos.
 * Incluye validaciones de negocio para asegurar la coherencia de los datos.
 * 
 * Reglas de negocio:
 * - La tarifa por hora debe ser mayor a 0
 * - El mínimo de horas cobrables debe ser al menos 1
 * - La tarifa máxima diaria debe ser mayor o igual a la tarifa por hora
 * - Solo puede haber una configuración activa por tipo de vehículo
 */
@Data
@Builder
public class RateConfig {
    
    private Long id;
    private Long vehicleTypeId;
    private BigDecimal ratePerHour;        // Tarifa por hora (ej: 2000.00)
    private Integer minimumChargeHours;    // Mínimo de horas a cobrar (ej: 1)
    private BigDecimal maximumDailyRate;   // Tarifa máxima diaria (ej: 15000.00)
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Valida que la tarifa por hora sea válida
     * @return true si la tarifa es mayor a 0
     */
    public boolean isValidRatePerHour() {
        return ratePerHour != null && ratePerHour.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Valida que el mínimo de horas sea válido
     * @return true si el mínimo es mayor a 0
     */
    public boolean isValidMinimumChargeHours() {
        return minimumChargeHours != null && minimumChargeHours > 0;
    }

    /**
     * Valida que la tarifa máxima diaria sea válida
     * @return true si la tarifa máxima es nula o mayor/igual a la tarifa por hora
     */
    public boolean isValidMaximumDailyRate() {
        if (maximumDailyRate == null) {
            return true; // Es opcional
        }
        return ratePerHour != null && maximumDailyRate.compareTo(ratePerHour) >= 0;
    }

    /**
     * Valida que el vehicleTypeId sea válido
     * @return true si el ID del tipo de vehículo no es nulo y es positivo
     */
    public boolean isValidVehicleTypeId() {
        return vehicleTypeId != null && vehicleTypeId > 0;
    }

    /**
     * Valida todas las reglas de negocio de la entidad
     * @return true si todos los campos son válidos
     */
    public boolean isValid() {
        return isValidRatePerHour() && 
               isValidMinimumChargeHours() && 
               isValidMaximumDailyRate() && 
               isValidVehicleTypeId();
    }

    /**
     * Calcula el monto a cobrar basado en las horas estacionado
     * @param hoursParked horas que estuvo estacionado el vehículo
     * @return monto calculado aplicando las reglas de negocio
     */
    public BigDecimal calculateAmount(double hoursParked) {
        if (!isValid()) {
            throw new IllegalStateException("Cannot calculate amount with invalid rate configuration");
        }

        // Aplicar mínimo de horas
        double hoursToCharge = Math.max(hoursParked, minimumChargeHours);
        
        // Redondear hacia arriba las horas (fracción de hora se cobra completa)
        int hoursToChargeRounded = (int) Math.ceil(hoursToCharge);
        
        // Calcular monto base
        BigDecimal baseAmount = ratePerHour.multiply(BigDecimal.valueOf(hoursToChargeRounded));
        
        // Aplicar tarifa máxima diaria si existe
        if (maximumDailyRate != null && baseAmount.compareTo(maximumDailyRate) > 0) {
            return maximumDailyRate;
        }
        
        return baseAmount;
    }
}
