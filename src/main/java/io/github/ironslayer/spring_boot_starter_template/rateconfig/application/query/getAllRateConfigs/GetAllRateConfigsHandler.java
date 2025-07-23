package io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getAllRateConfigs;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler para obtener todas las configuraciones de tarifas
 * Accesible para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetAllRateConfigsHandler implements RequestHandler<GetAllRateConfigsRequest, GetAllRateConfigsResponse> {

    private final RateConfigRepository rateConfigRepository;

    @Override
    public GetAllRateConfigsResponse handle(GetAllRateConfigsRequest request) {

        List<RateConfig> rateConfigs = rateConfigRepository.findAll();

        return new GetAllRateConfigsResponse(rateConfigs);
    }

    @Override
    public Class<GetAllRateConfigsRequest> getRequestType() {
        return GetAllRateConfigsRequest.class;
    }
}
