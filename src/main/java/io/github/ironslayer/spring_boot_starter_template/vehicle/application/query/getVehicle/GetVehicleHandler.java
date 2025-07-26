package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getVehicle;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler para obtener un vehículo por ID.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetVehicleHandler implements RequestHandler<GetVehicleRequest, GetVehicleResponse> {
    
    private final VehicleRepository vehicleRepository;
    
    @Override
    public GetVehicleResponse handle(GetVehicleRequest request) {
        // Validar entrada
        validateRequest(request);
        
        // Buscar vehículo
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(request.vehicleId()));
        
        return new GetVehicleResponse(vehicle);
    }
    
    @Override
    public Class<GetVehicleRequest> getRequestType() {
        return GetVehicleRequest.class;
    }
    
    private void validateRequest(GetVehicleRequest request) {
        if (request.vehicleId() == null) {
            throw new BadRequestException("Vehicle ID cannot be null");
        }
        
        if (request.vehicleId() <= 0) {
            throw new BadRequestException("Vehicle ID must be positive");
        }
    }
}
