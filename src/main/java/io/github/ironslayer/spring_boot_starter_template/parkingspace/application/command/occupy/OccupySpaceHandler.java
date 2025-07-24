package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.occupy;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception.ParkingSpaceNotAvailableException;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception.ParkingSpaceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para ocupar un espacio de parqueo con un vehículo
 * Accesible para ADMIN y OPERATOR
 * 
 * Valida que el espacio esté disponible antes de ocuparlo
 */
@Component
@RequiredArgsConstructor
@Transactional
public class OccupySpaceHandler implements RequestHandler<OccupySpaceRequest, Void> {
    
    private final ParkingSpaceRepository parkingSpaceRepository;
    
    @Override
    public Void handle(OccupySpaceRequest request) {
        // Validaciones de entrada
        validateRequest(request);
        
        // Buscar el espacio de parqueo
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(request.parkingSpaceId())
                .orElseThrow(() -> new ParkingSpaceNotFoundException(request.parkingSpaceId()));
        
        // Verificar que no haya otro vehículo con la misma placa ya parqueado
        parkingSpaceRepository.findByOccupiedByVehiclePlate(request.vehiclePlate().toUpperCase().trim())
                .ifPresent(occupiedSpace -> {
                    throw new BadRequestException("Vehicle with plate " + request.vehiclePlate() + 
                        " is already parked in space " + occupiedSpace.getSpaceNumber());
                });
        
        try {
            // Usar la lógica de dominio para ocupar el espacio
            parkingSpace.occupy(request.vehiclePlate());
            
            // Guardar los cambios
            parkingSpaceRepository.save(parkingSpace);
            
        } catch (IllegalStateException e) {
            // Convertir excepciones de dominio a excepciones de aplicación
            throw new ParkingSpaceNotAvailableException(parkingSpace.getSpaceNumber(), e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Class<OccupySpaceRequest> getRequestType() {
        return OccupySpaceRequest.class;
    }
    
    private void validateRequest(OccupySpaceRequest request) {
        if (request.parkingSpaceId() == null) {
            throw new BadRequestException("Parking space ID cannot be null");
        }
        
        if (request.parkingSpaceId() <= 0) {
            throw new BadRequestException("Parking space ID must be positive");
        }
        
        if (request.vehiclePlate() == null || request.vehiclePlate().trim().isEmpty()) {
            throw new BadRequestException("Vehicle plate cannot be null or empty");
        }
        
        // Validación básica del formato de placa (puedes ajustar según tu país)
        String plate = request.vehiclePlate().trim();
        if (plate.length() < 3 || plate.length() > 10) {
            throw new BadRequestException("Vehicle plate must be between 3 and 10 characters");
        }
    }
}
