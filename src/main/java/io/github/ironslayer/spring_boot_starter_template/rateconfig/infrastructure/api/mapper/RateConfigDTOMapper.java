package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.mapper;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.createRateConfig.CreateRateConfigRequest;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.updateRateConfig.UpdateRateConfigRequest;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getAllRateConfigs.GetAllRateConfigsResponse;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfig.GetRateConfigResponse;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfigByVehicleType.GetRateConfigByVehicleTypeResponse;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto.CreateRateConfigRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto.RateConfigResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto.RateConfigUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper para convertir entre DTOs, comandos/queries y entidades de dominio
 * 
 * Usa MapStruct para generar automáticamente las implementaciones,
 * facilitando el mantenimiento y reduciendo el código boilerplate
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RateConfigDTOMapper {

    // ===============================
    // CREATE OPERATIONS
    // ===============================
    
    /**
     * Convierte DTO de creación a comando
     */
    default CreateRateConfigRequest toCreateCommand(CreateRateConfigRequestDTO dto) {
        RateConfig rateConfig = RateConfig.builder()
                .vehicleTypeId(dto.getVehicleTypeId())
                .ratePerHour(dto.getRatePerHour())
                .minimumChargeHours(dto.getMinimumChargeHours())
                .maximumDailyRate(dto.getMaximumDailyRate())
                .isActive(true) // Siempre true en creación
                .build();
        
        return new CreateRateConfigRequest(rateConfig);
    }

    // ===============================
    // UPDATE OPERATIONS
    // ===============================
    
    /**
     * Convierte ID y DTO de actualización a comando
     */
    default UpdateRateConfigRequest toUpdateCommand(Long id, RateConfigUpdateDTO dto) {
        RateConfig rateConfig = RateConfig.builder()
                .id(id)
                .ratePerHour(dto.getRatePerHour())
                .minimumChargeHours(dto.getMinimumChargeHours())
                .maximumDailyRate(dto.getMaximumDailyRate())
                .isActive(dto.getIsActive())
                .build();
        
        return new UpdateRateConfigRequest(rateConfig);
    }

    // ===============================
    // RESPONSE OPERATIONS
    // ===============================
    
    /**
     * Convierte respuesta de query individual a DTO
     */
    default RateConfigResponseDTO toResponseDTO(GetRateConfigResponse response) {
        return toResponseDTO(response.rateConfig());
    }
    
    /**
     * Convierte respuesta de query por tipo de vehículo a DTO
     */
    default RateConfigResponseDTO toResponseDTO(GetRateConfigByVehicleTypeResponse response) {
        return toResponseDTO(response.rateConfig());
    }
    
    /**
     * Convierte entidad de dominio a DTO de respuesta
     */
    RateConfigResponseDTO toResponseDTO(RateConfig rateConfig);
    
    /**
     * Convierte lista de entidades a lista de DTOs
     */
    List<RateConfigResponseDTO> toResponseDTOList(List<RateConfig> rateConfigs);
    
    /**
     * Convierte respuesta de query múltiple a lista de DTOs
     */
    default List<RateConfigResponseDTO> toResponseDTOList(GetAllRateConfigsResponse response) {
        return toResponseDTOList(response.rateConfigs());
    }
}
