package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAvailableSpaces;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler para obtener espacios de parqueo disponibles
 * Accesible para ADMIN y OPERATOR
 * 
 * Puede filtrar por tipo de vehículo si se especifica
 */
@Component
@RequiredArgsConstructor
public class GetAvailableSpacesHandler implements RequestHandler<GetAvailableSpacesRequest, GetAvailableSpacesResponse> {
    
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    
    @Override
    public GetAvailableSpacesResponse handle(GetAvailableSpacesRequest request) {
        List<ParkingSpace> availableSpaces;
        long totalCount;
        
        if (request.vehicleTypeId() != null) {
            // Validar que el tipo de vehículo existe
            if (!vehicleTypeRepository.findById(request.vehicleTypeId()).isPresent()) {
                throw new VehicleTypeNotFoundException(request.vehicleTypeId());
            }
            
            // Obtener espacios disponibles para el tipo de vehículo específico
            availableSpaces = parkingSpaceRepository.findAllAvailableByVehicleType(request.vehicleTypeId());
            totalCount = parkingSpaceRepository.countAvailableByVehicleType(request.vehicleTypeId());
        } else {
            // Obtener todos los espacios disponibles
            availableSpaces = parkingSpaceRepository.findAllAvailable();
            totalCount = parkingSpaceRepository.countAvailable();
        }
        
        return new GetAvailableSpacesResponse(availableSpaces, totalCount);
    }
    
    @Override
    public Class<GetAvailableSpacesRequest> getRequestType() {
        return GetAvailableSpacesRequest.class;
    }
}
