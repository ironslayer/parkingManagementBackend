package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getAllVehicles;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler para obtener todos los vehículos.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetAllVehiclesHandler implements RequestHandler<GetAllVehiclesRequest, GetAllVehiclesResponse> {
    
    private final VehicleRepository vehicleRepository;
    
    @Override
    public GetAllVehiclesResponse handle(GetAllVehiclesRequest request) {
        List<Vehicle> vehicles;
        
        // Decidir si obtener solo activos o todos
        if (Boolean.TRUE.equals(request.activeOnly())) {
            vehicles = vehicleRepository.findAllActive();
        } else {
            vehicles = vehicleRepository.findAll();
        }
        
        return new GetAllVehiclesResponse(vehicles);
    }
    
    @Override
    public Class<GetAllVehiclesRequest> getRequestType() {
        return GetAllVehiclesRequest.class;
    }
}
