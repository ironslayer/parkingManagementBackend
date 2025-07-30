package io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo de dominio para los reportes de ingresos del parqueadero
 */
@Data
@Builder
public class RevenueReport {
    
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
    
    /**
     * Calcula el promedio de ingresos por pago
     */
    public BigDecimal calculateAveragePayment() {
        if (totalPayments == null || totalPayments == 0) {
            return BigDecimal.ZERO;
        }
        
        if (totalRevenue == null) {
            return BigDecimal.ZERO;
        }
        
        return totalRevenue.divide(BigDecimal.valueOf(totalPayments), 2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Obtiene el tipo de vehículo que genera más ingresos
     */
    public String getMostProfitableVehicleType() {
        BigDecimal maxRevenue = BigDecimal.ZERO;
        String mostProfitable = "N/A";
        
        if (carRevenue != null && carRevenue.compareTo(maxRevenue) > 0) {
            maxRevenue = carRevenue;
            mostProfitable = "CAR";
        }
        
        if (motorcycleRevenue != null && motorcycleRevenue.compareTo(maxRevenue) > 0) {
            maxRevenue = motorcycleRevenue;
            mostProfitable = "MOTORCYCLE";
        }
        
        if (truckRevenue != null && truckRevenue.compareTo(maxRevenue) > 0) {
            maxRevenue = truckRevenue;
            mostProfitable = "TRUCK";
        }
        
        return mostProfitable;
    }
}
