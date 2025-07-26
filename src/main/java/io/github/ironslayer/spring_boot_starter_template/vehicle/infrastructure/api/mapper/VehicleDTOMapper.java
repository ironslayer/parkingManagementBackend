package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.mapper;

import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto.CreateVehicleRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto.UpdateVehicleRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto.VehicleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper para convertir entre entidades de dominio Vehicle y DTOs de API.
 */
@Mapper(componentModel = "spring")
public interface VehicleDTOMapper {
    
    /**
     * Convierte CreateVehicleRequestDTO a entidad de dominio.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vehicle toDomain(CreateVehicleRequestDTO createDTO);
    
    /**
     * Convierte UpdateVehicleRequestDTO a entidad de dominio.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "licensePlate", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vehicle toDomain(UpdateVehicleRequestDTO updateDTO);
    
    /**
     * Convierte entidad de dominio a DTO de respuesta.
     */
    VehicleResponseDTO toResponseDTO(Vehicle vehicle);
    
    /**
     * Convierte lista de entidades de dominio a lista de DTOs de respuesta.
     */
    List<VehicleResponseDTO> toResponseDTOList(List<Vehicle> vehicles);
}
