package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfig;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.exception.RateConfigNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler para obtener una configuración de tarifa por ID
 * Accesible para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetRateConfigHandler implements RequestHandler<GetRateConfigRequest, GetRateConfigResponse> {

    private final RateConfigRepository rateConfigRepository;

    @Override
    public GetRateConfigResponse handle(GetRateConfigRequest request) {

        RateConfig rateConfig = rateConfigRepository.findById(request.rateConfigId())
                .orElseThrow(() -> new RateConfigNotFoundException(request.rateConfigId()));

        return new GetRateConfigResponse(rateConfig);
    }

    @Override
    public Class<GetRateConfigRequest> getRequestType() {
        return GetRateConfigRequest.class;
    }
}
