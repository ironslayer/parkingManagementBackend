package io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para reportes de ocupación del parqueadero
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyReportDto {
    
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
     * Número de vehículos que entraron en la fecha
     */
    private Integer vehiclesEntered;
    
    /**
     * Número de vehículos que salieron en la fecha
     */
    private Integer vehiclesExited;
    
    /**
     * Máxima ocupación alcanzada en el día
     */
    private Integer peakOccupancy;
    
    /**
     * Indica si el parqueadero estuvo cerca de la capacidad máxima
     */
    private Boolean nearCapacity;
}
