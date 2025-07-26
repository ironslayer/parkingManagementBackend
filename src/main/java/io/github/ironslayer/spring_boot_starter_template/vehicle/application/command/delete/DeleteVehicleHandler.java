package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.delete;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para eliminar (desactivar) un vehículo.
 * Realiza un soft delete desactivando el vehículo en lugar de eliminarlo físicamente.
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 */
@Component
@RequiredArgsConstructor
@Transactional
public class DeleteVehicleHandler implements RequestHandler<DeleteVehicleRequest, Void> {
    
    private final VehicleRepository vehicleRepository;
    
    @Override
    public Void handle(DeleteVehicleRequest request) {
        // Validar entrada
        validateRequest(request);
        
        // Buscar vehículo
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(request.vehicleId()));
        
        // Verificar que no esté ya desactivado
        if (!vehicle.isActive()) {
            throw new BadRequestException("Vehicle with ID " + request.vehicleId() + " is already inactive");
        }
        
        // Desactivar usando lógica de dominio
        vehicle.deactivate();
        
        // Guardar cambios
        vehicleRepository.save(vehicle);
        
        return null;
    }
    
    @Override
    public Class<DeleteVehicleRequest> getRequestType() {
        return DeleteVehicleRequest.class;
    }
    
    private void validateRequest(DeleteVehicleRequest request) {
        if (request.vehicleId() == null) {
            throw new BadRequestException("Vehicle ID cannot be null");
        }
        
        if (request.vehicleId() <= 0) {
            throw new BadRequestException("Vehicle ID must be positive");
        }
    }
}
