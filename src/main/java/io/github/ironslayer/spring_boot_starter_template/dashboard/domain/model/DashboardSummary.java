package io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de dominio para el dashboard principal con métricas en tiempo real
 */
@Data
@Builder
public class DashboardSummary {
    
    /**
     * Fecha y hora de generación del dashboard
     */
    private LocalDateTime generatedAt;
    
    /**
     * Total de espacios en el parqueadero
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
     * Porcentaje de ocupación actual
     */
    private BigDecimal currentOccupancyPercentage;
    
    /**
     * Número de vehículos que han ingresado hoy
     */
    private Integer todayEntries;
    
    /**
     * Número de vehículos que han salido hoy
     */
    private Integer todayExits;
    
    /**
     * Ingresos totales del día actual
     */
    private BigDecimal todayRevenue;
    
    /**
     * Número de pagos procesados hoy
     */
    private Integer todayPayments;
    
    /**
     * Promedio de tiempo de permanencia en horas
     */
    private BigDecimal averageStayDuration;
    
    /**
     * Número de sesiones activas (vehículos sin salir)
     */
    private Integer activeSessions;
    
    /**
     * Indica si el sistema está operando normalmente
     */
    private Boolean systemHealthy;
    
    /**
     * Calcula el porcentaje de ocupación actual
     */
    public BigDecimal calculateOccupancyPercentage() {
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
     * Determina el estado de ocupación del parqueadero
     */
    public String getOccupancyStatus() {
        BigDecimal percentage = calculateOccupancyPercentage();
        
        if (percentage.compareTo(BigDecimal.valueOf(90)) > 0) {
            return "CRITICAL"; // Más del 90%
        } else if (percentage.compareTo(BigDecimal.valueOf(75)) > 0) {
            return "HIGH"; // 75-90%
        } else if (percentage.compareTo(BigDecimal.valueOf(50)) > 0) {
            return "MODERATE"; // 50-75%
        } else {
            return "LOW"; // Menos del 50%
        }
    }
    
    /**
     * Calcula la rotación de vehículos (entradas + salidas)
     */
    public Integer getTotalVehicleMovements() {
        int entries = todayEntries != null ? todayEntries : 0;
        int exits = todayExits != null ? todayExits : 0;
        return entries + exits;
    }
}
