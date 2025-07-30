package io.github.ironslayer.spring_boot_starter_template.dashboard.application.usecase;

import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.RevenueReportDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.mapper.DashboardMapper;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.RevenueReport;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso para generar reportes de ingresos del parqueadero
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateRevenueReportUseCase {
    
    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;
    
    /**
     * Genera un reporte de ingresos para una fecha específica
     *
     * @param date fecha del reporte
     * @return reporte de ingresos
     */
    @Transactional(readOnly = true)
    public RevenueReportDto executeForDate(LocalDate date) {
        log.info("Generating revenue report for date: {}", date);
        
        try {
            // Validar que la fecha no sea futura
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Cannot generate report for future date: " + date);
            }
            
            // Obtener reporte desde el repositorio
            RevenueReport revenueReport = dashboardRepository.getRevenueReportByDate(date);
            
            // Convertir a DTO
            RevenueReportDto result = dashboardMapper.toDto(revenueReport);
            
            log.info("Revenue report generated for {}: ${} total revenue, {} payments",
                    date, result.getTotalRevenue(), result.getTotalPayments());
                    
            return result;
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for revenue report: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating revenue report for date: {}", date, e);
            throw new RuntimeException("Failed to generate revenue report", e);
        }
    }
    
    /**
     * Genera reportes de ingresos para un rango de fechas
     *
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return lista de reportes de ingresos
     */
    @Transactional(readOnly = true)
    public List<RevenueReportDto> executeForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Generating revenue reports for date range: {} to {}", startDate, endDate);
        
        try {
            // Validar rango de fechas
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            
            if (endDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("End date cannot be in the future");
            }
            
            // Limitar el rango a máximo 90 días para evitar sobrecarga
            if (startDate.plusDays(90).isBefore(endDate)) {
                throw new IllegalArgumentException("Date range cannot exceed 90 days");
            }
            
            // Obtener reportes desde el repositorio
            List<RevenueReport> revenueReports = dashboardRepository
                    .getRevenueReportsByDateRange(startDate, endDate);
            
            // Convertir a DTOs
            List<RevenueReportDto> result = dashboardMapper.toDtoList(revenueReports);
            
            log.info("Generated {} revenue reports for date range {} to {}",
                    result.size(), startDate, endDate);
                    
            return result;
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for revenue reports: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating revenue reports for date range: {} to {}", startDate, endDate, e);
            throw new RuntimeException("Failed to generate revenue reports", e);
        }
    }
}
