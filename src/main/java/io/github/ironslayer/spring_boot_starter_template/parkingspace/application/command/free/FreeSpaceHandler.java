package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.free;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception.ParkingSpaceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para liberar un espacio de parqueo
 * Accesible para ADMIN y OPERATOR
 * 
 * Valida que el espacio esté ocupado antes de liberarlo
 */
@Component
@RequiredArgsConstructor
@Transactional
public class FreeSpaceHandler implements RequestHandler<FreeSpaceRequest, Void> {
    
    private final ParkingSpaceRepository parkingSpaceRepository;
    
    @Override
    public Void handle(FreeSpaceRequest request) {
        // Validaciones de entrada
        validateRequest(request);
        
        // Buscar el espacio de parqueo
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(request.parkingSpaceId())
                .orElseThrow(() -> new ParkingSpaceNotFoundException(request.parkingSpaceId()));
        
        try {
            // Usar la lógica de dominio para liberar el espacio
            parkingSpace.free();
            
            // Guardar los cambios
            parkingSpaceRepository.save(parkingSpace);
            
        } catch (IllegalStateException e) {
            // Convertir excepciones de dominio a excepciones de aplicación
            throw new BadRequestException(e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Class<FreeSpaceRequest> getRequestType() {
        return FreeSpaceRequest.class;
    }
    
    private void validateRequest(FreeSpaceRequest request) {
        if (request.parkingSpaceId() == null) {
            throw new BadRequestException("Parking space ID cannot be null");
        }
        
        if (request.parkingSpaceId() <= 0) {
            throw new BadRequestException("Parking space ID must be positive");
        }
    }
}
