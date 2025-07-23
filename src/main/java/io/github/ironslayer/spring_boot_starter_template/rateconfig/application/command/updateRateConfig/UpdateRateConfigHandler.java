package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.updateRateConfig;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.exception.RateConfigNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Handler para actualizar configuraciones de tarifas existentes
 * SOLO usuarios con rol ADMIN pueden ejecutar esta operación
 * 
 * Implementa actualización parcial donde solo se modifican los campos no nulos
 * Mantiene las mismas validaciones de negocio que en la creación
 */
@Component
@RequiredArgsConstructor
public class UpdateRateConfigHandler implements RequestHandler<UpdateRateConfigRequest, Void> {

    private final RateConfigRepository rateConfigRepository;

    @Override
    public Void handle(UpdateRateConfigRequest request) {

        RateConfig rateConfig = request.rateConfig();

        // Verificar que existe
        RateConfig existing = rateConfigRepository.findById(rateConfig.getId())
                .orElseThrow(() -> new RateConfigNotFoundException(rateConfig.getId()));

        // Actualización parcial - solo actualizar campos no nulos
        if (rateConfig.getRatePerHour() != null) {
            // Validar tarifa por hora si se proporciona
            if (!rateConfig.isValidRatePerHour()) {
                throw new BadRequestException("Rate per hour must be greater than 0");
            }
            existing.setRatePerHour(rateConfig.getRatePerHour());
        }

        if (rateConfig.getMinimumChargeHours() != null) {
            // Validar mínimo de horas si se proporciona
            if (!rateConfig.isValidMinimumChargeHours()) {
                throw new BadRequestException("Minimum charge hours must be greater than 0");
            }
            existing.setMinimumChargeHours(rateConfig.getMinimumChargeHours());
        }

        if (rateConfig.getMaximumDailyRate() != null) {
            existing.setMaximumDailyRate(rateConfig.getMaximumDailyRate());
        }

        // Validar que la configuración actualizada sea coherente
        if (!existing.isValidMaximumDailyRate()) {
            throw new BadRequestException("Maximum daily rate must be greater than or equal to rate per hour");
        }

        if (rateConfig.getIsActive() != null) {
            // Si se está activando, desactivar otras configuraciones del mismo tipo
            if (rateConfig.getIsActive() && !existing.getIsActive()) {
                if (rateConfigRepository.existsActiveByVehicleTypeIdAndIdNot(existing.getVehicleTypeId(), existing.getId())) {
                    rateConfigRepository.deactivateAllByVehicleTypeId(existing.getVehicleTypeId());
                }
            }
            existing.setIsActive(rateConfig.getIsActive());
        }

        existing.setUpdatedAt(LocalDateTime.now());

        rateConfigRepository.save(existing);

        return null;
    }

    @Override
    public Class<UpdateRateConfigRequest> getRequestType() {
        return UpdateRateConfigRequest.class;
    }
}
