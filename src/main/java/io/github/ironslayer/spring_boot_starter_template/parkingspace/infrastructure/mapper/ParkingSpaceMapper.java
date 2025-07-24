package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.mapper;

import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.entity.ParkingSpaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between ParkingSpace domain entities and ParkingSpaceEntity JPA entities.
 */
@Mapper(componentModel = "spring")
public interface ParkingSpaceMapper {
    
    /**
     * Convert domain entity to JPA entity.
     */
    @Mapping(source = "occupied", target = "isOccupied")
    @Mapping(source = "active", target = "isActive")
    ParkingSpaceEntity toEntity(ParkingSpace parkingSpace);
    
    /**
     * Convert JPA entity to domain entity.
     */
    @Mapping(source = "isOccupied", target = "occupied")
    @Mapping(source = "isActive", target = "active")
    ParkingSpace toDomain(ParkingSpaceEntity parkingSpaceEntity);
    
    /**
     * Update existing JPA entity with domain entity data.
     * Useful for update operations.
     */
    @Mapping(source = "occupied", target = "isOccupied")
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget ParkingSpaceEntity target, ParkingSpace source);
}
