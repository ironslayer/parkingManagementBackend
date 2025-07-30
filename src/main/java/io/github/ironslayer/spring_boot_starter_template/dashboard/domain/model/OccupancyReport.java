package io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo de dominio para las métricas de ocupación del parqueadero
 */
@Data
@Builder
public class OccupancyReport {
    
    /**
     * Fecha del reporte
     */
    private LocalDate reportDate;
    
    /**
     * Total de espacios disponibles en el sistema
     */
    private Integer totalSpaces;
    
    /**
     * Espacios actualmente ocupados
     */
    private Integer occupiedSpaces;
    
    /**
     * Espacios disponibles
     */
    private Integer availableSpaces;
    
    /**
     * Porcentaje de ocupación (0-100)
     */
    private BigDecimal occupancyPercentage;
    
    /**
     * Número de vehículos que entraron hoy
     */
    private Integer vehiclesEntered;
    
    /**
     * Número de vehículos que salieron hoy
     */
    private Integer vehiclesExited;
    
    /**
     * Máxima ocupación alcanzada en el día
     */
    private Integer peakOccupancy;
    
    /**
     * Calcula el porcentaje de ocupación actual
     */
    public BigDecimal calculateCurrentOccupancyPercentage() {
        if (totalSpaces == null || totalSpaces == 0) {
            return BigDecimal.ZERO;
        }
        
        if (occupiedSpaces == null) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(occupiedSpaces)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalSpaces), 2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Verifica si el parqueadero está casi lleno (>90% ocupación)
     */
    public boolean isNearCapacity() {
        BigDecimal currentOccupancy = calculateCurrentOccupancyPercentage();
        return currentOccupancy.compareTo(BigDecimal.valueOf(90)) > 0;
    }
}
