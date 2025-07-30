package io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para el resumen del dashboard principal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    
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
     * Estado de ocupación (LOW, MODERATE, HIGH, CRITICAL)
     */
    private String occupancyStatus;
    
    /**
     * Número de vehículos que han ingresado hoy
     */
    private Integer todayEntries;
    
    /**
     * Número de vehículos que han salido hoy
     */
    private Integer todayExits;
    
    /**
     * Total de movimientos de vehículos hoy
     */
    private Integer totalMovements;
    
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
}
