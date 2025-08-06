package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.changestatus;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para cambiar el estado de activación de un vehículo.
 * Permite activar o desactivar un vehículo según sea necesario.
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 */
@Component
@RequiredArgsConstructor
@Transactional
public class ChangeVehicleStatusHandler implements RequestHandler<ChangeVehicleStatusRequest, Void> {
    
    private final VehicleRepository vehicleRepository;
    
    @Override
    public Void handle(ChangeVehicleStatusRequest request) {
        // Validar entrada
        validateRequest(request);
        
        // Buscar vehículo
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(request.vehicleId()));
        
        // Verificar si el cambio es necesario
        if (vehicle.isActive() == request.isActive()) {
            String status = request.isActive() ? "active" : "inactive";
            throw new BadRequestException("Vehicle with ID " + request.vehicleId() + " is already " + status);
        }
        
        // Cambiar estado usando lógica de dominio
        if (request.isActive()) {
            vehicle.activate();
        } else {
            vehicle.deactivate();
        }
        
        // Guardar cambios
        vehicleRepository.save(vehicle);
        
        return null;
    }
    
    @Override
    public Class<ChangeVehicleStatusRequest> getRequestType() {
        return ChangeVehicleStatusRequest.class;
    }
    
    private void validateRequest(ChangeVehicleStatusRequest request) {
        if (request.vehicleId() == null) {
            throw new BadRequestException("Vehicle ID cannot be null");
        }
        
        if (request.vehicleId() <= 0) {
            throw new BadRequestException("Vehicle ID must be positive");
        }
        
        if (request.isActive() == null) {
            throw new BadRequestException("Status cannot be null");
        }
    }
}
