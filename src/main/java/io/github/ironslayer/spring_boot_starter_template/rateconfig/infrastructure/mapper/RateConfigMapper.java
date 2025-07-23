package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.mapper;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.entity.RateConfigEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio y JPA
 * 
 * Implementa la conversión bidireccional manteniendo la separación
 * entre las capas de dominio e infraestructura
 */
@Component
public class RateConfigMapper {

    /**
     * Convierte de entidad de dominio a entidad JPA
     */
    public RateConfigEntity toEntity(RateConfig rateConfig) {
        if (rateConfig == null) {
            return null;
        }

        RateConfigEntity entity = new RateConfigEntity();
        entity.setId(rateConfig.getId());
        entity.setVehicleTypeId(rateConfig.getVehicleTypeId());
        entity.setRatePerHour(rateConfig.getRatePerHour());
        entity.setMinimumChargeHours(rateConfig.getMinimumChargeHours());
        entity.setMaximumDailyRate(rateConfig.getMaximumDailyRate());
        entity.setIsActive(rateConfig.getIsActive());
        entity.setCreatedAt(rateConfig.getCreatedAt());
        entity.setUpdatedAt(rateConfig.getUpdatedAt());

        return entity;
    }

    /**
     * Convierte de entidad JPA a entidad de dominio
     */
    public RateConfig toDomain(RateConfigEntity entity) {
        if (entity == null) {
            return null;
        }

        return RateConfig.builder()
                .id(entity.getId())
                .vehicleTypeId(entity.getVehicleTypeId())
                .ratePerHour(entity.getRatePerHour())
                .minimumChargeHours(entity.getMinimumChargeHours())
                .maximumDailyRate(entity.getMaximumDailyRate())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
