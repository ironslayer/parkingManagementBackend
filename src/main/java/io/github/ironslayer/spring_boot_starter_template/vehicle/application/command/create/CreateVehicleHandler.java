package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.create;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleLicensePlateAlreadyExistsException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler para crear un nuevo vehículo.
 * Valida que la placa sea única y que el tipo de vehículo exista.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Transactional
public class CreateVehicleHandler implements RequestHandler<CreateVehicleRequest, CreateVehicleResponse> {
    
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    
    @Override
    public CreateVehicleResponse handle(CreateVehicleRequest request) {
        Vehicle vehicle = request.vehicle();
        
        // Validar entrada
        validateVehicle(vehicle);
        
        // Normalizar placa
        String normalizedLicensePlate = vehicle.getLicensePlate().toUpperCase().trim();
        
        // Verificar que la placa no exista
        if (vehicleRepository.existsByLicensePlate(normalizedLicensePlate)) {
            throw new VehicleLicensePlateAlreadyExistsException(normalizedLicensePlate);
        }
        
        // Validar que el tipo de vehículo existe
        if (!vehicleTypeRepository.findById(vehicle.getVehicleTypeId()).isPresent()) {
            throw new VehicleTypeNotFoundException(vehicle.getVehicleTypeId());
        }
        
        // Crear nuevo vehículo con datos normalizados
        Vehicle newVehicle = new Vehicle(
            normalizedLicensePlate,
            vehicle.getVehicleTypeId(),
            vehicle.getBrand(),
            vehicle.getModel(),
            vehicle.getColor(),
            vehicle.getOwnerName(),
            vehicle.getOwnerPhone()
        );
        
        // Validar formato de placa usando lógica de dominio
        if (!newVehicle.isValidLicensePlate()) {
            throw new BadRequestException("Invalid license plate format: " + normalizedLicensePlate + 
                ". License plate must be 3-15 alphanumeric characters (A-Z, 0-9, -)");
        }
        
        // Guardar vehículo
        Vehicle savedVehicle = vehicleRepository.save(newVehicle);
        
        return new CreateVehicleResponse(savedVehicle.getId());
    }
    
    @Override
    public Class<CreateVehicleRequest> getRequestType() {
        return CreateVehicleRequest.class;
    }
    
    private void validateVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new BadRequestException("Vehicle cannot be null");
        }
        
        if (vehicle.getLicensePlate() == null || vehicle.getLicensePlate().trim().isEmpty()) {
            throw new BadRequestException("License plate cannot be null or empty");
        }
        
        if (vehicle.getVehicleTypeId() == null) {
            throw new BadRequestException("Vehicle type ID cannot be null");
        }
        
        if (vehicle.getVehicleTypeId() <= 0) {
            throw new BadRequestException("Vehicle type ID must be positive");
        }
        
        // Validaciones opcionales pero recomendadas
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
}
