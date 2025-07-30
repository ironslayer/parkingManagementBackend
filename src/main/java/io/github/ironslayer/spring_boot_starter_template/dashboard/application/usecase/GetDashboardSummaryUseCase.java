package io.github.ironslayer.spring_boot_starter_template.dashboard.application.usecase;

import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.DashboardSummaryDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.mapper.DashboardMapper;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.DashboardSummary;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para obtener el resumen del dashboard principal
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetDashboardSummaryUseCase {
    
    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;
    
    /**
     * Obtiene el resumen completo del dashboard con métricas en tiempo real
     *
     * @return resumen del dashboard
     */
    @Transactional(readOnly = true)
    public DashboardSummaryDto execute() {
        log.info("Generating dashboard summary");
        
        try {
            // Obtener resumen del dashboard desde el repositorio
            DashboardSummary dashboardSummary = dashboardRepository.getDashboardSummary();
            
            // Convertir a DTO
            DashboardSummaryDto result = dashboardMapper.toDto(dashboardSummary);
            
            log.info("Dashboard summary generated successfully. Occupancy: {}%, Revenue: ${}",
                    result.getCurrentOccupancyPercentage(),
                    result.getTodayRevenue());
                    
            return result;
            
        } catch (Exception e) {
            log.error("Error generating dashboard summary", e);
            throw new RuntimeException("Failed to generate dashboard summary", e);
        }
    }
}
