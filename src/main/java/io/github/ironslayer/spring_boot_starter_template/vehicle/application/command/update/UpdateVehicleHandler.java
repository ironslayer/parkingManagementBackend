package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.update;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Handler para actualizar un vehículo existente.
 * Permite actualizar toda la información excepto la placa (que es immutable).
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Transactional
public class UpdateVehicleHandler implements RequestHandler<UpdateVehicleRequest, Void> {
    
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    
    @Override
    public Void handle(UpdateVehicleRequest request) {
        Vehicle vehicleData = request.vehicle();
        
        // Validar entrada
        validateVehicleUpdate(vehicleData);
        
        // Buscar vehículo existente
        Vehicle existingVehicle = vehicleRepository.findById(vehicleData.getId())
                .orElseThrow(() -> new VehicleNotFoundException(vehicleData.getId()));
        
        // Validar que el tipo de vehículo existe si se está cambiando
        if (vehicleData.getVehicleTypeId() != null && 
            !vehicleData.getVehicleTypeId().equals(existingVehicle.getVehicleTypeId())) {
            if (!vehicleTypeRepository.findById(vehicleData.getVehicleTypeId()).isPresent()) {
                throw new VehicleTypeNotFoundException(vehicleData.getVehicleTypeId());
            }
        }
        
        // Actualizar solo campos no nulos usando lógica de dominio selectiva
        updateSelectiveFields(existingVehicle, vehicleData);
        
        // Guardar cambios
        vehicleRepository.save(existingVehicle);
        
        return null;
    }
    
    @Override
    public Class<UpdateVehicleRequest> getRequestType() {
        return UpdateVehicleRequest.class;
    }
    
    private void validateVehicleUpdate(Vehicle vehicle) {
        if (vehicle == null) {
            throw new BadRequestException("Vehicle cannot be null");
        }
        
        if (vehicle.getId() == null) {
            throw new BadRequestException("Vehicle ID cannot be null for update");
        }
        
        if (vehicle.getId() <= 0) {
            throw new BadRequestException("Vehicle ID must be positive");
        }
        
        // Validar vehicle type ID solo si se proporciona
        if (vehicle.getVehicleTypeId() != null && vehicle.getVehicleTypeId() <= 0) {
            throw new BadRequestException("Vehicle type ID must be positive");
        }
        
        // Validaciones de longitud solo para campos no nulos
        if (vehicle.getOwnerName() != null && vehicle.getOwnerName().trim().length() > 100) {
            throw new BadRequestException("Owner name cannot exceed 100 characters");
        }
        
        if (vehicle.getOwnerPhone() != null && vehicle.getOwnerPhone().trim().length() > 20) {
            throw new BadRequestException("Owner phone cannot exceed 20 characters");
        }
        
        if (vehicle.getBrand() != null && vehicle.getBrand().trim().length() > 50) {
            throw new BadRequestException("Brand cannot exceed 50 characters");
        }
        
        if (vehicle.getModel() != null && vehicle.getModel().trim().length() > 50) {
            throw new BadRequestException("Model cannot exceed 50 characters");
        }
        
        if (vehicle.getColor() != null && vehicle.getColor().trim().length() > 30) {
            throw new BadRequestException("Color cannot exceed 30 characters");
        }
    }
    
    /**
     * Actualiza selectivamente solo los campos que no son nulos en vehicleData.
     */
    private void updateSelectiveFields(Vehicle existingVehicle, Vehicle vehicleData) {
        // Actualizar vehicle type solo si se proporciona
        if (vehicleData.getVehicleTypeId() != null) {
            existingVehicle.setVehicleTypeId(vehicleData.getVehicleTypeId());
        }
        
        // Actualizar brand solo si se proporciona
        if (vehicleData.getBrand() != null) {
            existingVehicle.setBrand(vehicleData.getBrand().trim());
        }
        
        // Actualizar model solo si se proporciona
        if (vehicleData.getModel() != null) {
            existingVehicle.setModel(vehicleData.getModel().trim());
        }
        
        // Actualizar color solo si se proporciona
        if (vehicleData.getColor() != null) {
            existingVehicle.setColor(vehicleData.getColor().trim());
        }
        
        // Actualizar owner name solo si se proporciona
        if (vehicleData.getOwnerName() != null) {
            existingVehicle.setOwnerName(vehicleData.getOwnerName().trim());
        }
        
        // Actualizar owner phone solo si se proporciona
        if (vehicleData.getOwnerPhone() != null) {
            existingVehicle.setOwnerPhone(vehicleData.getOwnerPhone().trim());
        }
        
        // Actualizar timestamp
        existingVehicle.setUpdatedAt(LocalDateTime.now());
    }
}
