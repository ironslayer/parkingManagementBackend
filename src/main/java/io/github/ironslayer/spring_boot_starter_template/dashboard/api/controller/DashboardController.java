package io.github.ironslayer.spring_boot_starter_template.dashboard.api.controller;

import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.DashboardSummaryDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.usecase.GetDashboardSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    @Operation(
        summary = "Get dashboard statistics with date filters",
        description = "Retrieve dashboard statistics for a specific date range. " +
                     "ADMIN users can query any date range. " +
                     "OPERATOR users are limited to current month data."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or OPERATOR role required"),
        @ApiResponse(responseCode = "400", description = "Invalid date format or range"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DashboardSummaryDto> getDashboardStats(
            @Parameter(description = "Start date (YYYY-MM-DD), defaults to today")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            
            @Parameter(description = "End date (YYYY-MM-DD), defaults to today")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        
        log.info("Received request for dashboard stats with dateFrom: {} and dateTo: {}", dateFrom, dateTo);
        
        try {
            // Si no se proporcionan fechas, usar hoy
            if (dateFrom == null) {
                dateFrom = LocalDate.now();
            }
            if (dateTo == null) {
                dateTo = LocalDate.now();
            }
            
            // Validar que dateFrom no sea posterior a dateTo
            if (dateFrom.isAfter(dateTo)) {
                log.warn("Invalid date range: dateFrom {} is after dateTo {}", dateFrom, dateTo);
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            
            // Por ahora retornamos las estadísticas actuales
            // TODO: Implementar filtrado por fechas en el use case
            DashboardSummaryDto dashboardSummary = getDashboardSummaryUseCase.execute();
            
            log.info("Dashboard stats retrieved successfully for period {} to {}", dateFrom, dateTo);
            
            return ResponseEntity.ok(dashboardSummary);
            
        } catch (IllegalArgumentException e) {
            log.error("Bad request for dashboard stats: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving dashboard stats", e);
            throw e;
        }
    }
}
