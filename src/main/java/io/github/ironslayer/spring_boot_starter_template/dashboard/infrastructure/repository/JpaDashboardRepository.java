package io.github.ironslayer.spring_boot_starter_template.dashboard.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.DashboardSummary;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.OccupancyReport;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.RevenueReport;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.NonNull;

/**
 * Implementación JPA del repositorio de dashboard
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaDashboardRepository implements DashboardRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public DashboardSummary getDashboardSummary() {
        log.debug("Getting dashboard summary");
        
        // Consulta principal para métricas del dashboard
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM parking_spaces) as total_spaces,
                (SELECT COUNT(*) FROM parking_sessions WHERE exit_time IS NULL) as occupied_spaces,
                (SELECT COUNT(*) FROM parking_sessions WHERE exit_time IS NULL) as active_sessions,
                (SELECT COUNT(*) FROM parking_sessions WHERE DATE(entry_time) = CURRENT_DATE) as today_entries,
                (SELECT COUNT(*) FROM parking_sessions WHERE DATE(exit_time) = CURRENT_DATE) as today_exits,
                (SELECT COALESCE(SUM(total_amount), 0) FROM payments WHERE DATE(paid_at) = CURRENT_DATE) as today_revenue,
                (SELECT COUNT(*) FROM payments WHERE DATE(paid_at) = CURRENT_DATE) as today_payments,
                (SELECT COALESCE(AVG(EXTRACT(EPOCH FROM (exit_time - entry_time))/3600), 0) 
                 FROM parking_sessions 
                 WHERE exit_time IS NOT NULL AND DATE(exit_time) = CURRENT_DATE) as avg_stay_duration
            """;
        
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Integer totalSpaces = rs.getInt("total_spaces");
            Integer occupiedSpaces = rs.getInt("occupied_spaces");
            Integer availableSpaces = totalSpaces - occupiedSpaces;
            
            return DashboardSummary.builder()
                    .generatedAt(LocalDateTime.now())
                    .totalSpaces(totalSpaces)
                    .occupiedSpaces(occupiedSpaces)
                    .availableSpaces(availableSpaces)
                    .currentOccupancyPercentage(calculateOccupancyPercentage(occupiedSpaces, totalSpaces))
                    .todayEntries(rs.getInt("today_entries"))
                    .todayExits(rs.getInt("today_exits"))
                    .todayRevenue(rs.getBigDecimal("today_revenue"))
                    .todayPayments(rs.getInt("today_payments"))
                    .averageStayDuration(rs.getBigDecimal("avg_stay_duration"))
                    .activeSessions(rs.getInt("active_sessions"))
                    .systemHealthy(true) // Podríamos implementar lógica más compleja aquí
                    .build();
        });
    }
    
    @Override
    public OccupancyReport getOccupancyReportByDate(LocalDate date) {
        log.debug("Getting occupancy report for date: {}", date);
        
        String sql = """
            SELECT 
                ? as report_date,
                (SELECT COUNT(*) FROM parking_spaces) as total_spaces,
                (SELECT COUNT(*) FROM parking_sessions WHERE DATE(entry_time) <= ? AND (exit_time IS NULL OR DATE(exit_time) >= ?)) as occupied_spaces,
                (SELECT COUNT(*) FROM parking_sessions WHERE DATE(entry_time) = ?) as vehicles_entered,
                (SELECT COUNT(*) FROM parking_sessions WHERE DATE(exit_time) = ?) as vehicles_exited,
                (SELECT MAX(concurrent_sessions.session_count) 
                 FROM (
                     SELECT COUNT(*) as session_count
                     FROM parking_sessions ps1
                     WHERE ps1.entry_time <= ? + INTERVAL '1 day' 
                       AND (ps1.exit_time IS NULL OR ps1.exit_time >= ?)
                     GROUP BY DATE_TRUNC('hour', ps1.entry_time)
                 ) concurrent_sessions) as peak_occupancy
            """;
        
        return jdbcTemplate.queryForObject(sql, 
            (rs, rowNum) -> {
                Integer totalSpaces = rs.getInt("total_spaces");
                Integer occupiedSpaces = rs.getInt("occupied_spaces");
                Integer availableSpaces = totalSpaces - occupiedSpaces;
                BigDecimal occupancyPercentage = calculateOccupancyPercentage(occupiedSpaces, totalSpaces);
                
                return OccupancyReport.builder()
                        .reportDate(date)
                        .totalSpaces(totalSpaces)
                        .occupiedSpaces(occupiedSpaces)
                        .availableSpaces(availableSpaces)
                        .occupancyPercentage(occupancyPercentage)
                        .vehiclesEntered(rs.getInt("vehicles_entered"))
                        .vehiclesExited(rs.getInt("vehicles_exited"))
                        .peakOccupancy(rs.getInt("peak_occupancy"))
                        .build();
            }, date, date, date, date, date, date, date);
    }
    
    @Override
    public List<OccupancyReport> getOccupancyReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting occupancy reports for date range: {} to {}", startDate, endDate);
        
        String sql = """
            WITH date_series AS (
                SELECT generate_series(?::date, ?::date, '1 day'::interval)::date as report_date
            )
            SELECT 
                ds.report_date,
                (SELECT COUNT(*) FROM parking_spaces) as total_spaces,
                COALESCE(daily_stats.vehicles_entered, 0) as vehicles_entered,
                COALESCE(daily_stats.vehicles_exited, 0) as vehicles_exited,
                COALESCE(daily_stats.peak_occupancy, 0) as peak_occupancy
            FROM date_series ds
            LEFT JOIN (
                SELECT 
                    DATE(entry_time) as date,
                    COUNT(*) as vehicles_entered,
                    COUNT(CASE WHEN exit_time IS NOT NULL AND DATE(exit_time) = DATE(entry_time) THEN 1 END) as vehicles_exited,
                    MAX(CASE WHEN exit_time IS NULL THEN 1 ELSE 0 END) as peak_occupancy
                FROM parking_sessions
                WHERE DATE(entry_time) BETWEEN ? AND ?
                GROUP BY DATE(entry_time)
            ) daily_stats ON ds.report_date = daily_stats.date
            ORDER BY ds.report_date
            """;
        
        return jdbcTemplate.query(sql, 
            new OccupancyReportRowMapper(), startDate, endDate, startDate, endDate);
    }
    
    @Override
    public RevenueReport getRevenueReportByDate(LocalDate date) {
        log.debug("Getting revenue report for date: {}", date);
        
        String sql = """
            SELECT 
                ? as report_date,
                COALESCE(SUM(p.total_amount), 0) as total_revenue,
                COUNT(p.id) as total_payments,
                COALESCE(SUM(CASE WHEN vt.name = 'CAR' THEN p.total_amount ELSE 0 END), 0) as car_revenue,
                COALESCE(SUM(CASE WHEN vt.name = 'MOTORCYCLE' THEN p.total_amount ELSE 0 END), 0) as motorcycle_revenue,
                COALESCE(SUM(CASE WHEN vt.name = 'TRUCK' THEN p.total_amount ELSE 0 END), 0) as truck_revenue,
                COUNT(CASE WHEN vt.name = 'CAR' THEN 1 END) as car_payments,
                COUNT(CASE WHEN vt.name = 'MOTORCYCLE' THEN 1 END) as motorcycle_payments,
                COUNT(CASE WHEN vt.name = 'TRUCK' THEN 1 END) as truck_payments
            FROM payments p
            JOIN parking_sessions ps ON p.parking_session_id = ps.id
            JOIN vehicles v ON ps.vehicle_id = v.id
            JOIN vehicle_types vt ON v.vehicle_type_id = vt.id
            WHERE DATE(p.paid_at) = ?
            """;
        
        return jdbcTemplate.queryForObject(sql, 
            (rs, rowNum) -> {
                BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                Integer totalPayments = rs.getInt("total_payments");
                
                return RevenueReport.builder()
                        .reportDate(date)
                        .totalRevenue(totalRevenue)
                        .totalPayments(totalPayments)
                        .averagePayment(calculateAveragePayment(totalRevenue, totalPayments))
                        .carRevenue(rs.getBigDecimal("car_revenue"))
                        .motorcycleRevenue(rs.getBigDecimal("motorcycle_revenue"))
                        .truckRevenue(rs.getBigDecimal("truck_revenue"))
                        .carPayments(rs.getInt("car_payments"))
                        .motorcyclePayments(rs.getInt("motorcycle_payments"))
                        .truckPayments(rs.getInt("truck_payments"))
                        .build();
            }, date, date);
    }
    
    @Override
    public List<RevenueReport> getRevenueReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting revenue reports for date range: {} to {}", startDate, endDate);
        
        String sql = """
            WITH date_series AS (
                SELECT generate_series(?::date, ?::date, '1 day'::interval)::date as report_date
            )
            SELECT 
                ds.report_date,
                COALESCE(daily_revenue.total_revenue, 0) as total_revenue,
                COALESCE(daily_revenue.total_payments, 0) as total_payments,
                COALESCE(daily_revenue.car_revenue, 0) as car_revenue,
                COALESCE(daily_revenue.motorcycle_revenue, 0) as motorcycle_revenue,
                COALESCE(daily_revenue.truck_revenue, 0) as truck_revenue,
                COALESCE(daily_revenue.car_payments, 0) as car_payments,
                COALESCE(daily_revenue.motorcycle_payments, 0) as motorcycle_payments,
                COALESCE(daily_revenue.truck_payments, 0) as truck_payments
            FROM date_series ds
            LEFT JOIN (
                SELECT 
                    DATE(p.paid_at) as payment_date,
                    SUM(p.total_amount) as total_revenue,
                    COUNT(p.id) as total_payments,
                    SUM(CASE WHEN vt.name = 'CAR' THEN p.total_amount ELSE 0 END) as car_revenue,
                    SUM(CASE WHEN vt.name = 'MOTORCYCLE' THEN p.total_amount ELSE 0 END) as motorcycle_revenue,
                    SUM(CASE WHEN vt.name = 'TRUCK' THEN p.total_amount ELSE 0 END) as truck_revenue,
                    COUNT(CASE WHEN vt.name = 'CAR' THEN 1 END) as car_payments,
                    COUNT(CASE WHEN vt.name = 'MOTORCYCLE' THEN 1 END) as motorcycle_payments,
                    COUNT(CASE WHEN vt.name = 'TRUCK' THEN 1 END) as truck_payments
                FROM payments p
                JOIN parking_sessions ps ON p.parking_session_id = ps.id
                JOIN vehicles v ON ps.vehicle_id = v.id
                JOIN vehicle_types vt ON v.vehicle_type_id = vt.id
                WHERE DATE(p.paid_at) BETWEEN ? AND ?
                GROUP BY DATE(p.paid_at)
            ) daily_revenue ON ds.report_date = daily_revenue.payment_date
            ORDER BY ds.report_date
            """;
        
        return jdbcTemplate.query(sql, 
            new RevenueReportRowMapper(), startDate, endDate, startDate, endDate);
    }
    
    @Override
    public Integer getTotalSpaces() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM parking_spaces", Integer.class);
    }
    
    @Override
    public Integer getOccupiedSpaces() {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM parking_sessions WHERE exit_time IS NULL", Integer.class);
    }
    
    @Override
    public Integer getActiveSessions() {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM parking_sessions WHERE exit_time IS NULL", Integer.class);
    }
    
    // Métodos auxiliares
    
    private BigDecimal calculateOccupancyPercentage(Integer occupied, Integer total) {
        if (total == null || total == 0) {
            return BigDecimal.ZERO;
        }
        if (occupied == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(occupied)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, java.math.RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateAveragePayment(BigDecimal totalRevenue, Integer totalPayments) {
        if (totalPayments == null || totalPayments == 0) {
            return BigDecimal.ZERO;
        }
        if (totalRevenue == null) {
            return BigDecimal.ZERO;
        }
        return totalRevenue.divide(BigDecimal.valueOf(totalPayments), 2, java.math.RoundingMode.HALF_UP);
    }
    
    // Row Mappers
    
    private static class OccupancyReportRowMapper implements RowMapper<OccupancyReport> {
        @Override
        public OccupancyReport mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            LocalDate reportDate = rs.getDate("report_date").toLocalDate();
            Integer totalSpaces = rs.getInt("total_spaces");
            Integer vehiclesEntered = rs.getInt("vehicles_entered");
            Integer vehiclesExited = rs.getInt("vehicles_exited");
            Integer peakOccupancy = rs.getInt("peak_occupancy");
            
            // Para reportes históricos, ocupiedSpaces puede ser 0 si no hay sesiones activas
            Integer occupiedSpaces = Math.max(0, vehiclesEntered - vehiclesExited);
            Integer availableSpaces = totalSpaces - occupiedSpaces;
            
            BigDecimal occupancyPercentage = totalSpaces > 0 
                ? BigDecimal.valueOf(occupiedSpaces * 100.0 / totalSpaces).setScale(2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            
            return OccupancyReport.builder()
                    .reportDate(reportDate)
                    .totalSpaces(totalSpaces)
                    .occupiedSpaces(occupiedSpaces)
                    .availableSpaces(availableSpaces)
                    .occupancyPercentage(occupancyPercentage)
                    .vehiclesEntered(vehiclesEntered)
                    .vehiclesExited(vehiclesExited)
                    .peakOccupancy(peakOccupancy)
                    .build();
        }
    }
    
    private static class RevenueReportRowMapper implements RowMapper<RevenueReport> {
        @Override
        public RevenueReport mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            LocalDate reportDate = rs.getDate("report_date").toLocalDate();
            BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
            Integer totalPayments = rs.getInt("total_payments");
            
            BigDecimal averagePayment = totalPayments > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(totalPayments), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            
            return RevenueReport.builder()
                    .reportDate(reportDate)
                    .totalRevenue(totalRevenue)
                    .totalPayments(totalPayments)
                    .averagePayment(averagePayment)
                    .carRevenue(rs.getBigDecimal("car_revenue"))
                    .motorcycleRevenue(rs.getBigDecimal("motorcycle_revenue"))
                    .truckRevenue(rs.getBigDecimal("truck_revenue"))
                    .carPayments(rs.getInt("car_payments"))
                    .motorcyclePayments(rs.getInt("motorcycle_payments"))
                    .truckPayments(rs.getInt("truck_payments"))
                    .build();
        }
    }
}
