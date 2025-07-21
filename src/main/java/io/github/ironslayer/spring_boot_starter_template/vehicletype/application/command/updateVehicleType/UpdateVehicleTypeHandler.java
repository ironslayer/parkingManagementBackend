package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.updateVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Handler para actualizar tipos de vehículos existentes
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 */
@Component
@RequiredArgsConstructor
public class UpdateVehicleTypeHandler implements RequestHandler<UpdateVehicleTypeRequest, Void> {

    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public Void handle(UpdateVehicleTypeRequest request) {

        VehicleType vehicleType = request.vehicleType();

        // Verificar que existe
        VehicleType existing = vehicleTypeRepository.findById(vehicleType.getId())
                .orElseThrow(() -> new VehicleTypeNotFoundException(vehicleType.getId()));

        // Actualización parcial - solo actualizar campos no nulos
        if (vehicleType.getName() != null) {
            // Validar nombre si se proporciona
            if (!vehicleType.isValidName()) {
                throw new BadRequestException("Vehicle type name is invalid. Must be between 2 and 50 characters.");
            }
            
            // Verificar que el nombre no existe en otro registro
            vehicleTypeRepository.findByName(vehicleType.getName())
                    .ifPresent(found -> {
                        if (!found.getId().equals(vehicleType.getId())) {
                            throw new BadRequestException("Vehicle type with name '" + vehicleType.getName() + "' already exists.");
                        }
                    });
            
            existing.setName(vehicleType.getName());
        }
        
        if (vehicleType.getDescription() != null) {
            existing.setDescription(vehicleType.getDescription());
        }
        
        if (vehicleType.getIsActive() != null) {
            existing.setIsActive(vehicleType.getIsActive());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());

        vehicleTypeRepository.save(existing);

        return null;
    }

    @Override
    public Class<UpdateVehicleTypeRequest> getRequestType() {
        return UpdateVehicleTypeRequest.class;
    }
}
