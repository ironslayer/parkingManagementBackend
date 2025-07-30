package io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para reportes de ingresos del parqueadero
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDto {
    
    /**
     * Fecha del reporte
     */
    private LocalDate reportDate;
    
    /**
     * Ingresos totales del día
     */
    private BigDecimal totalRevenue;
    
    /**
     * Número total de pagos procesados
     */
    private Integer totalPayments;
    
    /**
     * Promedio de ingreso por pago
     */
    private BigDecimal averagePayment;
    
    /**
     * Tipo de vehículo más rentable
     */
    private String mostProfitableVehicleType;
    
    /**
     * Ingresos por vehículos tipo CAR
     */
    private BigDecimal carRevenue;
    
    /**
     * Ingresos por vehículos tipo MOTORCYCLE
     */
    private BigDecimal motorcycleRevenue;
    
    /**
     * Ingresos por vehículos tipo TRUCK
     */
    private BigDecimal truckRevenue;
    
    /**
     * Número de pagos por vehículos tipo CAR
     */
    private Integer carPayments;
    
    /**
     * Número de pagos por vehículos tipo MOTORCYCLE
     */
    private Integer motorcyclePayments;
    
    /**
     * Número de pagos por vehículos tipo TRUCK
     */
    private Integer truckPayments;
}
