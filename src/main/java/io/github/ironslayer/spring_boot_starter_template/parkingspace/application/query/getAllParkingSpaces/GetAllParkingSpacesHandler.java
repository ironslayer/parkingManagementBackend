package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAllParkingSpaces;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler para obtener todos los espacios de parqueo
 * Accesible para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
public class GetAllParkingSpacesHandler implements RequestHandler<GetAllParkingSpacesRequest, GetAllParkingSpacesResponse> {
    
    private final ParkingSpaceRepository parkingSpaceRepository;
    
    @Override
    public GetAllParkingSpacesResponse handle(GetAllParkingSpacesRequest request) {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findAll();
        return new GetAllParkingSpacesResponse(parkingSpaces);
    }
    
    @Override
    public Class<GetAllParkingSpacesRequest> getRequestType() {
        return GetAllParkingSpacesRequest.class;
    }
}
