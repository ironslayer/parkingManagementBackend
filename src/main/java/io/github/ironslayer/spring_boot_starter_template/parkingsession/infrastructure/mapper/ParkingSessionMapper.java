package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.mapper;

import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.entity.ParkingSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre entidades de dominio y entidades JPA.
 * Utiliza MapStruct para generar automáticamente las implementaciones.
 */
@Mapper(componentModel = "spring")
public interface ParkingSessionMapper {
    
    /**
     * Convierte de entidad de dominio a entidad JPA
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "parkingSpaceId", source = "parkingSpaceId")
    @Mapping(target = "entryTime", source = "entryTime")
    @Mapping(target = "exitTime", source = "exitTime")
    @Mapping(target = "operatorEntryId", source = "operatorEntryId")
    @Mapping(target = "operatorExitId", source = "operatorExitId")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "ticketCode", source = "ticketCode")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ParkingSessionEntity toEntity(ParkingSession domain);
    
        /**
     * Convierte de entidad JPA a entidad de dominio
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "parkingSpaceId", source = "parkingSpaceId")
    @Mapping(target = "entryTime", source = "entryTime")
    @Mapping(target = "exitTime", source = "exitTime")
    @Mapping(target = "operatorEntryId", source = "operatorEntryId")
    @Mapping(target = "operatorExitId", source = "operatorExitId")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "ticketCode", source = "ticketCode")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ParkingSession toDomain(ParkingSessionEntity entity);
}
