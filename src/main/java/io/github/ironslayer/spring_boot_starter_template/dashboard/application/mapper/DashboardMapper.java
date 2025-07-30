package io.github.ironslayer.spring_boot_starter_template.dashboard.application.mapper;

import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.DashboardSummaryDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.OccupancyReportDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.application.dto.RevenueReportDto;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.DashboardSummary;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.OccupancyReport;
import io.github.ironslayer.spring_boot_starter_template.dashboard.domain.model.RevenueReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para convertir entre modelos de dominio y DTOs del dashboard
 */
@Mapper(componentModel = "spring")
public interface DashboardMapper {
    
    /**
     * Convierte DashboardSummary a DashboardSummaryDto
     *
     * @param dashboardSummary modelo de dominio
     * @return DTO
     */
    @Mapping(target = "occupancyStatus", expression = "java(dashboardSummary.getOccupancyStatus())")
    @Mapping(target = "totalMovements", expression = "java(dashboardSummary.getTotalVehicleMovements())")
    DashboardSummaryDto toDto(DashboardSummary dashboardSummary);
    
    /**
     * Convierte OccupancyReport a OccupancyReportDto
     *
     * @param occupancyReport modelo de dominio
     * @return DTO
     */
    @Mapping(target = "nearCapacity", expression = "java(occupancyReport.isNearCapacity())")
    OccupancyReportDto toDto(OccupancyReport occupancyReport);
    
    /**
     * Convierte lista de OccupancyReport a lista de OccupancyReportDto
     *
     * @param occupancyReports lista de modelos de dominio
     * @return lista de DTOs
     */
    List<OccupancyReportDto> toDto(List<OccupancyReport> occupancyReports);
    
    /**
     * Convierte RevenueReport a RevenueReportDto
     *
     * @param revenueReport modelo de dominio
     * @return DTO
     */
    @Mapping(target = "mostProfitableVehicleType", expression = "java(revenueReport.getMostProfitableVehicleType())")
    RevenueReportDto toDto(RevenueReport revenueReport);
    
    /**
     * Convierte lista de RevenueReport a lista de RevenueReportDto
     *
     * @param revenueReports lista de modelos de dominio
     * @return lista de DTOs
     */
    List<RevenueReportDto> toDtoList(List<RevenueReport> revenueReports);
}
