package io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.findByLicensePlate;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler para buscar un vehículo por placa.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class FindVehicleByLicensePlateHandler implements RequestHandler<FindVehicleByLicensePlateRequest, FindVehicleByLicensePlateResponse> {
    
    private final VehicleRepository vehicleRepository;
    
    @Override
    public FindVehicleByLicensePlateResponse handle(FindVehicleByLicensePlateRequest request) {
        // Validar entrada
        validateRequest(request);
        
        // Normalizar placa
        String normalizedLicensePlate = request.licensePlate().toUpperCase().trim();
        
        // Buscar vehículo
        Vehicle vehicle = vehicleRepository.findByLicensePlate(normalizedLicensePlate)
                .orElseThrow(() -> VehicleNotFoundException.forLicensePlate(normalizedLicensePlate));
        
        return new FindVehicleByLicensePlateResponse(vehicle);
    }
    
    @Override
    public Class<FindVehicleByLicensePlateRequest> getRequestType() {
        return FindVehicleByLicensePlateRequest.class;
    }
    
    private void validateRequest(FindVehicleByLicensePlateRequest request) {
        if (request.licensePlate() == null || request.licensePlate().trim().isEmpty()) {
            throw new BadRequestException("License plate cannot be null or empty");
        }
        
        String plate = request.licensePlate().trim();
        if (plate.length() < 3 || plate.length() > 15) {
            throw new BadRequestException("License plate must be between 3 and 15 characters");
        }
    }
}
