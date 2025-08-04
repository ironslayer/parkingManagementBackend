package io.github.ironslayer.spring_boot_starter_template.dashboard.api.controller;

import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.DashboardSummaryDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.usecase.GetDashboardSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el dashboard principal del sistema de parqueadero
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard and real-time metrics endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {
    
    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;
    
    /**
     * Obtiene el resumen del dashboard con métricas en tiempo real
     * 
     * ADMIN: Acceso completo a todas las métricas del dashboard
     * OPERATOR: Acceso a métricas básicas del día actual solamente
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    @Operation(
        summary = "Get real-time dashboard summary",
        description = "Retrieve comprehensive dashboard metrics including occupancy rates, revenue, " +
                     "active sessions, and system status. " +
                     "ADMIN users see all metrics. " +
                     "OPERATOR users see current day metrics only."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Dashboard summary retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required - Please provide valid JWT token"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - ADMIN or OPERATOR role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error while generating dashboard metrics"
        )
    })
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        log.info("Received request to get dashboard summary");
        
        try {
            DashboardSummaryDto dashboardSummary = getDashboardSummaryUseCase.execute();
            
            log.info("Dashboard summary retrieved successfully. Occupancy: {}%, Revenue: ${}",
                    dashboardSummary.getCurrentOccupancyPercentage(),
                    dashboardSummary.getTodayRevenue());
            
            return ResponseEntity.ok(dashboardSummary);
            
        } catch (Exception e) {
            log.error("Error retrieving dashboard summary", e);
            throw e;
        }
    }
}
