package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.createRateConfig;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Handler para crear nuevas configuraciones de tarifas
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 * 
 * Reglas de negocio implementadas:
 * - Validar que todos los campos sean válidos según las reglas de dominio
 * - Solo puede haber una configuración activa por tipo de vehículo
 * - Desactivar configuraciones anteriores antes de crear la nueva
 */
@Component
@RequiredArgsConstructor
public class CreateRateConfigHandler implements RequestHandler<CreateRateConfigRequest, CreateRateConfigResponse> {

    private final RateConfigRepository rateConfigRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public CreateRateConfigResponse handle(CreateRateConfigRequest request) {

        RateConfig rateConfig = request.rateConfig();

        // Validar que el tipo de vehículo sea válido
        if (!rateConfig.isValidVehicleTypeId()) {
            throw new BadRequestException("Vehicle type ID must be a positive number");
        }

        // Validar que el tipo de vehículo exista en la base de datos y esté activo
        var vehicleType = vehicleTypeRepository.findById(rateConfig.getVehicleTypeId())
            .orElseThrow(() -> new BadRequestException("Vehicle type with ID " + rateConfig.getVehicleTypeId() + " does not exist"));
        
        if (!vehicleType.getIsActive()) {
            throw new BadRequestException("Vehicle type with ID " + rateConfig.getVehicleTypeId() + " is not active");
        }

        // Validar que no exista ya una configuración activa para este tipo de vehículo
        if (rateConfigRepository.existsActiveByVehicleTypeId(rateConfig.getVehicleTypeId())) {
            throw new BadRequestException("An active rate configuration already exists for vehicle type ID " + 
                rateConfig.getVehicleTypeId() + ". Please deactivate the existing configuration first.");
        }

        // Validar que la tarifa por hora sea válida
        if (!rateConfig.isValidRatePerHour()) {
            throw new BadRequestException("Rate per hour must be greater than 0");
        }

        // Validar que el mínimo de horas sea válido
        if (!rateConfig.isValidMinimumChargeHours()) {
            throw new BadRequestException("Minimum charge hours must be greater than 0");
        }

        // Validar que la tarifa máxima diaria sea válida
        if (!rateConfig.isValidMaximumDailyRate()) {
            throw new BadRequestException("Maximum daily rate must be greater than or equal to rate per hour (" + 
                rateConfig.getRatePerHour() + "). Current maximum daily rate: " + 
                rateConfig.getMaximumDailyRate());
        }

        // Validar que el tipo de vehículo sea válido
        if (!rateConfig.isValidVehicleTypeId()) {
            throw new BadRequestException("Vehicle type ID must be a positive number");
        }

        // Si se va a crear como activa, desactivar otras configuraciones del mismo tipo
        if (rateConfig.getIsActive() != null && rateConfig.getIsActive()) {
            if (rateConfigRepository.existsActiveByVehicleTypeId(rateConfig.getVehicleTypeId())) {
                // Desactivar todas las configuraciones activas para este tipo de vehículo
                rateConfigRepository.deactivateAllByVehicleTypeId(rateConfig.getVehicleTypeId());
            }
        }

        // Configurar datos de creación
        rateConfig.setIsActive(rateConfig.getIsActive() != null ? rateConfig.getIsActive() : true);
        rateConfig.setCreatedAt(LocalDateTime.now());
        rateConfig.setUpdatedAt(LocalDateTime.now());

        RateConfig saved = rateConfigRepository.save(rateConfig);

        return new CreateRateConfigResponse(saved.getId());
    }

    @Override
    public Class<CreateRateConfigRequest> getRequestType() {
        return CreateRateConfigRequest.class;
    }
}
