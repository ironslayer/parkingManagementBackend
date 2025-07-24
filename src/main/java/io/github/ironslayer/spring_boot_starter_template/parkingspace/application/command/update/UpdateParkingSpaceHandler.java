package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.update;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception.ParkingSpaceAlreadyExistsException;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception.ParkingSpaceNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Handler para actualizar espacios de parqueo existentes
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 * 
 * Implementa actualización parcial donde solo se modifican los campos no nulos
 * Mantiene las mismas validaciones de negocio que en la creación
 */
@Component
@RequiredArgsConstructor
@Transactional
public class UpdateParkingSpaceHandler implements RequestHandler<UpdateParkingSpaceRequest, Void> {
    
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    
    @Override
    public Void handle(UpdateParkingSpaceRequest request) {
        ParkingSpace parkingSpace = request.parkingSpace();
        
        // Verificar que el ID no sea nulo
        if (parkingSpace.getId() == null) {
            throw new BadRequestException("Parking space ID cannot be null for update operation");
        }
        
        // Verificar que existe
        ParkingSpace existing = parkingSpaceRepository.findById(parkingSpace.getId())
                .orElseThrow(() -> new ParkingSpaceNotFoundException(parkingSpace.getId()));
        
        // Actualización parcial - solo actualizar campos no nulos
        if (parkingSpace.getSpaceNumber() != null) {
            String normalizedSpaceNumber = parkingSpace.getSpaceNumber().toUpperCase().trim();
            
            // Validar formato usando lógica de dominio
            ParkingSpace tempSpace = new ParkingSpace();
            tempSpace.setSpaceNumber(normalizedSpaceNumber);
            if (!tempSpace.isValidSpaceNumber()) {
                throw new BadRequestException("Invalid space number format: " + normalizedSpaceNumber + 
                    ". Space number should contain letters followed by numbers (e.g., A001, B102)");
            }
            
            // Verificar que el número no existe en otro registro
            if (parkingSpaceRepository.existsBySpaceNumberAndIdNot(normalizedSpaceNumber, parkingSpace.getId())) {
                throw new ParkingSpaceAlreadyExistsException(normalizedSpaceNumber);
            }
            
            existing.setSpaceNumber(normalizedSpaceNumber);
        }
        
        if (parkingSpace.getVehicleTypeId() != null) {
            // Validar que el tipo de vehículo existe
            if (!vehicleTypeRepository.findById(parkingSpace.getVehicleTypeId()).isPresent()) {
                throw new VehicleTypeNotFoundException(parkingSpace.getVehicleTypeId());
            }
            
            existing.setVehicleTypeId(parkingSpace.getVehicleTypeId());
        }
        
        if (parkingSpace.isActive() != existing.isActive()) {
            if (parkingSpace.isActive()) {
                existing.activate();
            } else {
                // Solo se puede desactivar si no está ocupado
                if (existing.isOccupied()) {
                    throw new BadRequestException("Cannot deactivate occupied parking space " + existing.getSpaceNumber());
                }
                existing.deactivate();
            }
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        
        parkingSpaceRepository.save(existing);
        
        return null;
    }
    
    @Override
    public Class<UpdateParkingSpaceRequest> getRequestType() {
        return UpdateParkingSpaceRequest.class;
    }
}
