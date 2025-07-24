package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getParkingSpace;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.exception.ParkingSpaceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler para obtener un espacio de parqueo por ID
 * Accesible para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetParkingSpaceHandler implements RequestHandler<GetParkingSpaceRequest, GetParkingSpaceResponse> {
    
    private final ParkingSpaceRepository parkingSpaceRepository;
    
    @Override
    public GetParkingSpaceResponse handle(GetParkingSpaceRequest request) {
        // Validar entrada
        if (request.parkingSpaceId() == null) {
            throw new BadRequestException("Parking space ID cannot be null");
        }
        
        if (request.parkingSpaceId() <= 0) {
            throw new BadRequestException("Parking space ID must be positive");
        }
        
        // Buscar el espacio de parqueo
        ParkingSpace parkingSpace = parkingSpaceRepository.findById(request.parkingSpaceId())
                .orElseThrow(() -> new ParkingSpaceNotFoundException(request.parkingSpaceId()));
        
        return new GetParkingSpaceResponse(parkingSpace);
    }
    
    @Override
    public Class<GetParkingSpaceRequest> getRequestType() {
        return GetParkingSpaceRequest.class;
    }
}
