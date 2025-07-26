package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.mapper;

import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.entity.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper para convertir entre entidades de dominio Vehicle y entidades JPA VehicleEntity.
 */
@Mapper(componentModel = "spring")
public interface VehicleMapper {
    
    /**
     * Convierte de entidad de dominio a entidad JPA.
     */
    VehicleEntity toEntity(Vehicle vehicle);
    
    /**
     * Convierte de entidad JPA a entidad de dominio.
     */
    Vehicle toDomain(VehicleEntity vehicleEntity);
    
    /**
     * Actualiza una entidad JPA existente con datos de la entidad de dominio.
     * Ignora el ID y las fechas de creación para preservar la integridad.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget VehicleEntity target, Vehicle source);
}
