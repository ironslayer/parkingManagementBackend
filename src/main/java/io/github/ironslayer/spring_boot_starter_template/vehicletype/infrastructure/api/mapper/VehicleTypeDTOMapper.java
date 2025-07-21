package io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.mapper;

import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.createVehicleType.CreateVehicleTypeRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.createVehicleType.CreateVehicleTypeResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.updateVehicleType.UpdateVehicleTypeRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getAllVehicleTypes.GetAllVehicleTypesResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getVehicleType.GetVehicleTypeResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto.CreateVehicleTypeRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto.VehicleTypeResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto.VehicleTypeUpdateDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper para convertir entre DTOs de API y comandos/consultas
 */
@Component
public class VehicleTypeDTOMapper {

    // === MAPPERS DE REQUEST ===

    public CreateVehicleTypeRequest toCreateCommand(CreateVehicleTypeRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        VehicleType vehicleType = VehicleType.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(true)
                .build();
        return new CreateVehicleTypeRequest(vehicleType);
    }

    public UpdateVehicleTypeRequest toUpdateCommand(Long id, VehicleTypeUpdateDTO dto) {
        if (dto == null || id == null) {
            return null;
        }
        VehicleType vehicleType = VehicleType.builder()
                .id(id)
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive())
                .build();
        return new UpdateVehicleTypeRequest(vehicleType);
    }

    // === MAPPERS DE RESPONSE ===

    public VehicleTypeResponseDTO toResponseDTO(VehicleType vehicleType) {
        if (vehicleType == null) {
            return null;
        }
        return new VehicleTypeResponseDTO(
                vehicleType.getId(),
                vehicleType.getName(),
                vehicleType.getDescription(),
                vehicleType.getIsActive(),
                vehicleType.getCreatedAt(),
                vehicleType.getUpdatedAt()
        );
    }

    public VehicleTypeResponseDTO toResponseDTO(CreateVehicleTypeResponse response) {
        if (response == null) {
            return null;
        }
        // CreateVehicleTypeResponse solo contiene el ID, necesitamos obtener la entidad completa
        // Esto se manejará en el controller
        return new VehicleTypeResponseDTO(
                response.id(),
                null, // name será establecido en el controller
                null, // description será establecido en el controller  
                null, // isActive será establecido en el controller
                null, // createdAt será establecido en el controller
                null  // updatedAt será establecido en el controller
        );
    }

    public VehicleTypeResponseDTO toResponseDTO(GetVehicleTypeResponse response) {
        if (response == null || response.vehicleType() == null) {
            return null;
        }
        VehicleType vt = response.vehicleType();
        return new VehicleTypeResponseDTO(
                vt.getId(),
                vt.getName(),
                vt.getDescription(),
                vt.getIsActive(),
                vt.getCreatedAt(),
                vt.getUpdatedAt()
        );
    }

    public List<VehicleTypeResponseDTO> toResponseDTOList(GetAllVehicleTypesResponse response) {
        if (response == null || response.vehicleTypes() == null) {
            return List.of();
        }
        return response.vehicleTypes().stream()
                .map(vt -> new VehicleTypeResponseDTO(
                        vt.getId(),
                        vt.getName(),
                        vt.getDescription(),
                        vt.getIsActive(),
                        vt.getCreatedAt(),
                        vt.getUpdatedAt()
                ))
                .toList();
    }
}
