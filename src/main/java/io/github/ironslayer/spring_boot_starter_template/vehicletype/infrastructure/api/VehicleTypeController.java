package io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.createVehicleType.CreateVehicleTypeRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.createVehicleType.CreateVehicleTypeResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.command.updateVehicleType.UpdateVehicleTypeRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getAllVehicleTypes.GetAllVehicleTypesRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getAllVehicleTypes.GetAllVehicleTypesResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getVehicleType.GetVehicleTypeRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.application.query.getVehicleType.GetVehicleTypeResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto.CreateVehicleTypeRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto.VehicleTypeResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto.VehicleTypeUpdateDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.mapper.VehicleTypeDTOMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/vehicle-types")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "VehicleType", description = "The VehicleType API. Contains all the operations that can be performed on a vehicle type.")
@RequiredArgsConstructor
@RestController
@Slf4j
public class VehicleTypeController {

    private final Mediator mediator;
    private final VehicleTypeDTOMapper dtoMapper;

    @Operation(summary = "Find a vehicle type", description = "Find a vehicle type (ADMIN or OPERATOR)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<VehicleTypeResponseDTO> findById(@PathVariable Long id) {

        GetVehicleTypeResponse response = mediator.dispatch(new GetVehicleTypeRequest(id));

        VehicleTypeResponseDTO vehicleTypeDTO = dtoMapper.toResponseDTO(response);

        return ResponseEntity.ok(vehicleTypeDTO);
    }

    @Operation(summary = "List all vehicle types", description = "List all vehicle types (ADMIN or OPERATOR)")
    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<VehicleTypeResponseDTO>> findAll() {

        log.info("VehicleTypeController Get all vehicle types");

        GetAllVehicleTypesResponse response = mediator.dispatch(new GetAllVehicleTypesRequest());

        List<VehicleTypeResponseDTO> vehicleTypeDTOs = dtoMapper.toResponseDTOList(response);

        return ResponseEntity.ok(vehicleTypeDTOs);
    }

    @Operation(summary = "Create vehicle type", description = "Create vehicle type (ADMIN only)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VehicleTypeResponseDTO> createVehicleType(@Valid @RequestBody CreateVehicleTypeRequestDTO request) {
        
        CreateVehicleTypeRequest command = dtoMapper.toCreateCommand(request);
        CreateVehicleTypeResponse createResponse = mediator.dispatch(command);
        
        // Obtenemos la entidad creada para la respuesta
        GetVehicleTypeRequest query = new GetVehicleTypeRequest(createResponse.id());
        GetVehicleTypeResponse vehicleTypeResponse = mediator.dispatch(query);
        
        VehicleTypeResponseDTO responseDTO = dtoMapper.toResponseDTO(vehicleTypeResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Update a vehicle type", description = "Update a vehicle type (ADMIN only)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody VehicleTypeUpdateDTO updateDTO) {

        // Crear el command con el ID del parámetro y los campos del body
        UpdateVehicleTypeRequest command = dtoMapper.toUpdateCommand(id, updateDTO);

        mediator.dispatch(command);

        return ResponseEntity.noContent().build();
    }
}
