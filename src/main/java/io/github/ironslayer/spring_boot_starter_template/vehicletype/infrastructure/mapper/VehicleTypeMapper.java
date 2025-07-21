package io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.mapper;

import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.entity.VehicleTypeEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio y JPA
 */
@Component
public class VehicleTypeMapper {

    /**
     * Convierte de entidad de dominio a entidad JPA
     */
    public VehicleTypeEntity toEntity(VehicleType vehicleType) {
        if (vehicleType == null) {
            return null;
        }

        VehicleTypeEntity entity = new VehicleTypeEntity();
        entity.setId(vehicleType.getId());
        entity.setName(vehicleType.getName());
        entity.setDescription(vehicleType.getDescription());
        entity.setIsActive(vehicleType.getIsActive());
        entity.setCreatedAt(vehicleType.getCreatedAt());
        entity.setUpdatedAt(vehicleType.getUpdatedAt());

        return entity;
    }

    /**
     * Convierte de entidad JPA a entidad de dominio
     */
    public VehicleType toDomain(VehicleTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return VehicleType.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
