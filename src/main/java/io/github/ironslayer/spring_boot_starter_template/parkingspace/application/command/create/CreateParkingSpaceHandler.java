package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.create;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception.ParkingSpaceAlreadyExistsException;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for creating a new parking space.
 * Validates that the space number is unique and the vehicle type exists.
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 */
@Component
@RequiredArgsConstructor
@Transactional
public class CreateParkingSpaceHandler implements RequestHandler<CreateParkingSpaceRequest, CreateParkingSpaceResponse> {
    
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    
    @Override
    public CreateParkingSpaceResponse handle(CreateParkingSpaceRequest request) {
        ParkingSpace parkingSpace = request.parkingSpace();
        
        // Validate input
        validateParkingSpace(parkingSpace);
        
        // Normalize space number
        String normalizedSpaceNumber = parkingSpace.getSpaceNumber().toUpperCase().trim();
        
        // Check if space number already exists
        if (parkingSpaceRepository.existsBySpaceNumber(normalizedSpaceNumber)) {
            throw new ParkingSpaceAlreadyExistsException(normalizedSpaceNumber);
        }
        
        // Validate that vehicle type exists
        if (!vehicleTypeRepository.findById(parkingSpace.getVehicleTypeId()).isPresent()) {
            throw new VehicleTypeNotFoundException(parkingSpace.getVehicleTypeId());
        }
        
        // Create new parking space with normalized data
        ParkingSpace newParkingSpace = new ParkingSpace(normalizedSpaceNumber, parkingSpace.getVehicleTypeId());
        
        // Validate space number format using domain logic
        if (!newParkingSpace.isValidSpaceNumber()) {
            throw new BadRequestException("Invalid space number format: " + normalizedSpaceNumber + 
                ". Space number should contain letters followed by numbers (e.g., A001, B102)");
        }
        
        ParkingSpace saved = parkingSpaceRepository.save(newParkingSpace);
        
        return new CreateParkingSpaceResponse(saved.getId());
    }
    
    @Override
    public Class<CreateParkingSpaceRequest> getRequestType() {
        return CreateParkingSpaceRequest.class;
    }
    
    private void validateParkingSpace(ParkingSpace parkingSpace) {
        if (parkingSpace == null) {
            throw new BadRequestException("Parking space cannot be null");
        }
        
        if (parkingSpace.getSpaceNumber() == null || parkingSpace.getSpaceNumber().trim().isEmpty()) {
            throw new BadRequestException("Space number cannot be null or empty");
        }
        
        if (parkingSpace.getVehicleTypeId() == null) {
            throw new BadRequestException("Vehicle type ID cannot be null");
        }
        
        if (parkingSpace.getVehicleTypeId() <= 0) {
            throw new BadRequestException("Vehicle type ID must be positive");
        }
    }
}
