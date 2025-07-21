package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getVehicleType;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.exception.VehicleTypeNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler para obtener un tipo de vehículo por ID
 * Accesible para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetVehicleTypeHandler implements RequestHandler<GetVehicleTypeRequest, GetVehicleTypeResponse> {

    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public GetVehicleTypeResponse handle(GetVehicleTypeRequest request) {

        VehicleType vehicleType = vehicleTypeRepository.findById(request.vehicleTypeId())
                .orElseThrow(() -> new VehicleTypeNotFoundException(request.vehicleTypeId()));

        return new GetVehicleTypeResponse(vehicleType);
    }

    @Override
    public Class<GetVehicleTypeRequest> getRequestType() {
        return GetVehicleTypeRequest.class;
    }
}
