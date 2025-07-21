package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.createVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Handler para crear nuevos tipos de vehículos
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 */
@Component
@RequiredArgsConstructor
public class CreateVehicleTypeHandler implements RequestHandler<CreateVehicleTypeRequest, CreateVehicleTypeResponse> {

    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public CreateVehicleTypeResponse handle(CreateVehicleTypeRequest request) {

        VehicleType vehicleType = request.vehicleType();

        // Validaciones de negocio
        if (!vehicleType.isValidName()) {
            throw new BadRequestException("Vehicle type name is invalid. Must be between 2 and 50 characters.");
        }

        if (vehicleTypeRepository.existsByName(vehicleType.getName())) {
            throw new BadRequestException("Vehicle type with name '" + vehicleType.getName() + "' already exists.");
        }

        // Configurar datos de creación
        vehicleType.setIsActive(true);
        vehicleType.setCreatedAt(LocalDateTime.now());
        vehicleType.setUpdatedAt(LocalDateTime.now());

        VehicleType saved = vehicleTypeRepository.save(vehicleType);

        return new CreateVehicleTypeResponse(saved.getId());
    }

    @Override
    public Class<CreateVehicleTypeRequest> getRequestType() {
        return CreateVehicleTypeRequest.class;
    }
}
