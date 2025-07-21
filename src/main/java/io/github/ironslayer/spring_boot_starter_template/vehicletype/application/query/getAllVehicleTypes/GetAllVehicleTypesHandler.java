package io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getAllVehicleTypes;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler para obtener todos los tipos de vehículos
 * Accesible para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetAllVehicleTypesHandler implements RequestHandler<GetAllVehicleTypesRequest, GetAllVehicleTypesResponse> {

    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public GetAllVehicleTypesResponse handle(GetAllVehicleTypesRequest request) {

        List<VehicleType> vehicleTypes = vehicleTypeRepository.findAll();

        return new GetAllVehicleTypesResponse(vehicleTypes);
    }

    @Override
    public Class<GetAllVehicleTypesRequest> getRequestType() {
        return GetAllVehicleTypesRequest.class;
    }
}
