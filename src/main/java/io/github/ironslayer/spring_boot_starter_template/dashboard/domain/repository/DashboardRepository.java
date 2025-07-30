package io.github.ironslayer.spring_boot_starter_template.dashboard.domain.repository;

import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.DashboardSummary;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.OccupancyReport;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.RevenueReport;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de salida para operaciones de dashboard y reportes
 */
public interface DashboardRepository {
    
    /**
     * Obtiene el resumen del dashboard con métricas en tiempo real
     *
     * @return resumen del dashboard actual
     */
    DashboardSummary getDashboardSummary();
    
    /**
     * Genera un reporte de ocupación para una fecha específica
     *
     * @param date fecha del reporte
     * @return reporte de ocupación
     */
    OccupancyReport getOccupancyReportByDate(LocalDate date);
    
    /**
     * Genera reportes de ocupación para un rango de fechas
     *
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return lista de reportes de ocupación
     */
    List<OccupancyReport> getOccupancyReportsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Genera un reporte de ingresos para una fecha específica
     *
     * @param date fecha del reporte
     * @return reporte de ingresos
     */
    RevenueReport getRevenueReportByDate(LocalDate date);
    
    /**
     * Genera reportes de ingresos para un rango de fechas
     *
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return lista de reportes de ingresos
     */
    List<RevenueReport> getRevenueReportsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Obtiene el número total de espacios en el sistema
     *
     * @return total de espacios
     */
    Integer getTotalSpaces();
    
    /**
     * Obtiene el número de espacios actualmente ocupados
     *
     * @return espacios ocupados
     */
    Integer getOccupiedSpaces();
    
    /**
     * Obtiene el número de sesiones activas (vehículos sin salir)
     *
     * @return número de sesiones activas
     */
    Integer getActiveSessions();
}
