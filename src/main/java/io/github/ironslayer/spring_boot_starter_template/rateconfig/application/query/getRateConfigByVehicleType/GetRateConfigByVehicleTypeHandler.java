package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfigByVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.exception.RateConfigNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler para obtener la configuración de tarifa activa por tipo de vehículo
 * Muy útil para calcular tarifas durante el proceso de facturación
 * Accesible para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetRateConfigByVehicleTypeHandler implements RequestHandler<GetRateConfigByVehicleTypeRequest, GetRateConfigByVehicleTypeResponse> {

    private final RateConfigRepository rateConfigRepository;

    @Override
    public GetRateConfigByVehicleTypeResponse handle(GetRateConfigByVehicleTypeRequest request) {

        RateConfig rateConfig = rateConfigRepository.findActiveByVehicleTypeId(request.vehicleTypeId())
                .orElseThrow(() -> new RateConfigNotFoundException("No active rate configuration found for vehicle type ID: " + request.vehicleTypeId()));

        return new GetRateConfigByVehicleTypeResponse(rateConfig);
    }

    @Override
    public Class<GetRateConfigByVehicleTypeRequest> getRequestType() {
        return GetRateConfigByVehicleTypeRequest.class;
    }
}
