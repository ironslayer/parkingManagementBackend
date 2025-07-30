package io.github.ironslayer.spring_boot_starter_template.dashboard.application.usecase;

import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.OccupancyReportDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.mapper.DashboardMapper;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.OccupancyReport;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso para generar reportes de ocupación del parqueadero
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateOccupancyReportUseCase {
    
    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;
    
    /**
     * Genera un reporte de ocupación para una fecha específica
     *
     * @param date fecha del reporte
     * @return reporte de ocupación
     */
    @Transactional(readOnly = true)
    public OccupancyReportDto executeForDate(LocalDate date) {
        log.info("Generating occupancy report for date: {}", date);
        
        try {
            // Validar que la fecha no sea futura
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Cannot generate report for future date: " + date);
            }
            
            // Obtener reporte desde el repositorio
            OccupancyReport occupancyReport = dashboardRepository.getOccupancyReportByDate(date);
            
            // Convertir a DTO
            OccupancyReportDto result = dashboardMapper.toDto(occupancyReport);
            
            log.info("Occupancy report generated for {}: {}% occupancy, {} vehicles entered",
                    date, result.getOccupancyPercentage(), result.getVehiclesEntered());
                    
            return result;
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for occupancy report: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating occupancy report for date: {}", date, e);
            throw new RuntimeException("Failed to generate occupancy report", e);
        }
    }
    
    /**
     * Genera reportes de ocupación para un rango de fechas
     *
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return lista de reportes de ocupación
     */
    @Transactional(readOnly = true)
    public List<OccupancyReportDto> executeForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Generating occupancy reports for date range: {} to {}", startDate, endDate);
        
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
            List<OccupancyReport> occupancyReports = dashboardRepository
                    .getOccupancyReportsByDateRange(startDate, endDate);
            
            // Convertir a DTOs
            List<OccupancyReportDto> result = dashboardMapper.toDto(occupancyReports);
            
            log.info("Generated {} occupancy reports for date range {} to {}",
                    result.size(), startDate, endDate);
                    
            return result;
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for occupancy reports: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating occupancy reports for date range: {} to {}", startDate, endDate, e);
            throw new RuntimeException("Failed to generate occupancy reports", e);
        }
    }
}
