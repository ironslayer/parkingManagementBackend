package io.github.ironslayer.spring_boot_starter_template.dashboard.api.controller;

import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.OccupancyReportDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.RevenueReportDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.usecase.GenerateOccupancyReportUseCase;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.usecase.GenerateRevenueReportUseCase;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para reportes de ocupación e ingresos del parqueadero
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Occupancy and revenue reports endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportsController {
    
    private final GenerateOccupancyReportUseCase generateOccupancyReportUseCase;
    private final GenerateRevenueReportUseCase generateRevenueReportUseCase;
    
    /**
     * Obtiene un reporte de ocupación para una fecha específica
     * 
     * ADMIN: Acceso completo a todos los reportes de ocupación
     * OPERATOR: Acceso solo a reportes del día actual
     */
    @GetMapping("/occupancy")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('OPERATOR') and #date == T(java.time.LocalDate).now())")
    @Operation(
        summary = "Get occupancy report by date",
        description = "Generate occupancy report for a specific date. " +
                     "ADMIN users can access any date. " +
                     "OPERATOR users can only access today's report."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Occupancy report generated successfully"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid date parameter or future date requested"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - ADMIN role required for historical reports, OPERATOR can only access today's data"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error while generating report"
        )
    })
    public ResponseEntity<OccupancyReportDto> getOccupancyReport(
            @Parameter(description = "Report date in YYYY-MM-DD format", example = "2025-07-29", required = true)
            @RequestParam("date") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull(message = "Date parameter is required")
            @Valid LocalDate date) {
        
        log.info("Received request for occupancy report on date: {}", date);
        
        try {
            OccupancyReportDto report = generateOccupancyReportUseCase.executeForDate(date);
            
            log.info("Occupancy report generated successfully for {}: {}% occupancy",
                    date, report.getOccupancyPercentage());
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for occupancy report: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating occupancy report for date: {}", date, e);
            throw e;
        }
    }
    
    /**
     * Obtiene reportes de ocupación para un rango de fechas
     * 
     * ADMIN: Acceso completo a rangos de fechas (máximo 90 días)
     * OPERATOR: No tiene acceso a reportes por rango
     */
    @GetMapping("/occupancy/range")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Get occupancy reports by date range",
        description = "Generate occupancy reports for a date range (maximum 90 days). " +
                     "Only available for ADMIN users."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Occupancy reports generated successfully"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid date range (start > end, future dates, or range > 90 days)"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - ADMIN role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error while generating reports"
        )
    })
    public ResponseEntity<List<OccupancyReportDto>> getOccupancyReportRange(
            @Parameter(description = "Start date in YYYY-MM-DD format", example = "2025-07-01", required = true)
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull(message = "Start date parameter is required")
            @Valid LocalDate startDate,
            
            @Parameter(description = "End date in YYYY-MM-DD format", example = "2025-07-29", required = true)
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull(message = "End date parameter is required")
            @Valid LocalDate endDate) {
        
        log.info("Received request for occupancy reports from {} to {}", startDate, endDate);
        
        try {
            List<OccupancyReportDto> reports = generateOccupancyReportUseCase.executeForDateRange(startDate, endDate);
            
            log.info("Generated {} occupancy reports for date range {} to {}",
                    reports.size(), startDate, endDate);
            
            return ResponseEntity.ok(reports);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for occupancy reports: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating occupancy reports for range {} to {}", startDate, endDate, e);
            throw e;
        }
    }
    
    /**
     * Obtiene un reporte de ingresos para una fecha específica
     * 
     * ADMIN: Acceso completo a todos los reportes de ingresos
     * OPERATOR: Acceso solo a reportes del día actual
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('OPERATOR') and #date == T(java.time.LocalDate).now())")
    @Operation(
        summary = "Get revenue report by date",
        description = "Generate revenue report for a specific date. " +
                     "ADMIN users can access any date. " +
                     "OPERATOR users can only access today's report."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Revenue report generated successfully"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid date parameter or future date requested"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - ADMIN role required for historical reports, OPERATOR can only access today's data"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error while generating report"
        )
    })
    public ResponseEntity<RevenueReportDto> getRevenueReport(
            @Parameter(description = "Report date in YYYY-MM-DD format", example = "2025-07-29", required = true)
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull(message = "Date parameter is required")
            @Valid LocalDate date) {
        
        log.info("Received request for revenue report on date: {}", date);
        
        try {
            RevenueReportDto report = generateRevenueReportUseCase.executeForDate(date);
            
            log.info("Revenue report generated successfully for {}: ${} total revenue",
                    date, report.getTotalRevenue());
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for revenue report: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating revenue report for date: {}", date, e);
            throw e;
        }
    }
    
    /**
     * Obtiene reportes de ingresos para un rango de fechas
     * 
     * ADMIN: Acceso completo a rangos de fechas (máximo 90 días)
     * OPERATOR: No tiene acceso a reportes por rango
     */
    @GetMapping("/revenue/range")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Get revenue reports by date range",
        description = "Generate revenue reports for a date range (maximum 90 days). " +
                     "Only available for ADMIN users."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Revenue reports generated successfully"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid date range (start > end, future dates, or range > 90 days)"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - ADMIN role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error while generating reports"
        )
    })
    public ResponseEntity<List<RevenueReportDto>> getRevenueReportRange(
            @Parameter(description = "Start date in YYYY-MM-DD format", example = "2025-07-01", required = true)
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull(message = "Start date parameter is required")
            @Valid LocalDate startDate,
            
            @Parameter(description = "End date in YYYY-MM-DD format", example = "2025-07-29", required = true)
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull(message = "End date parameter is required")
            @Valid LocalDate endDate) {
        
        log.info("Received request for revenue reports from {} to {}", startDate, endDate);
        
        try {
            List<RevenueReportDto> reports = generateRevenueReportUseCase.executeForDateRange(startDate, endDate);
            
            log.info("Generated {} revenue reports for date range {} to {}",
                    reports.size(), startDate, endDate);
            
            return ResponseEntity.ok(reports);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for revenue reports: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating revenue reports for range {} to {}", startDate, endDate, e);
            throw e;
        }
    }
}
