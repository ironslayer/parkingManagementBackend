package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.changestatus.ChangeVehicleStatusRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.create.CreateVehicleRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.create.CreateVehicleResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.update.UpdateVehicleRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.findByLicensePlate.FindVehicleByLicensePlateRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.findByLicensePlate.FindVehicleByLicensePlateResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getAllVehicles.GetAllVehiclesRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getAllVehicles.GetAllVehiclesResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getVehicle.GetVehicleRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.query.getVehicle.GetVehicleResponse;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto.ChangeVehicleStatusRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto.CreateVehicleRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto.UpdateVehicleRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.dto.VehicleResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.api.mapper.VehicleDTOMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para operaciones de gestión de vehículos.
 * Proporciona endpoints para CRUD y búsquedas de vehículos.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Vehicles", description = "The Vehicle API. Contains all operations for managing vehicles.")
@RequiredArgsConstructor
public class VehicleController {
    
    private final Mediator mediator;
    private final VehicleDTOMapper dtoMapper;
    
    @Operation(summary = "Create a new vehicle", description = "Create a new vehicle (ADMIN and OPERATOR)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<VehicleResponseDTO> createVehicle(
            @Valid @RequestBody CreateVehicleRequestDTO requestDTO) {
        
        Vehicle vehicle = dtoMapper.toDomain(requestDTO);
        CreateVehicleRequest request = new CreateVehicleRequest(vehicle);
        CreateVehicleResponse response = mediator.dispatch(request);
        
        // Obtener el vehículo creado para devolver información completa
        GetVehicleRequest getRequest = new GetVehicleRequest(response.vehicleId());
        GetVehicleResponse getResponse = mediator.dispatch(getRequest);
        
        VehicleResponseDTO responseDTO = dtoMapper.toResponseDTO(getResponse.vehicle());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @Operation(summary = "Update an existing vehicle", description = "Partially update an existing vehicle (ADMIN and OPERATOR)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<Void> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequestDTO requestDTO) {
        
        // Crear entidad de dominio con ID del path parameter
        Vehicle vehicle = dtoMapper.toDomain(requestDTO);
        vehicle.setId(id); // Establecer ID desde URL path
        
        UpdateVehicleRequest request = new UpdateVehicleRequest(vehicle);
        mediator.dispatch(request);
        
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all vehicles", description = "Get all vehicles with optional filter for active only (ADMIN and OPERATOR)")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles(
            @Parameter(description = "Filter for active vehicles only")
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        
        GetAllVehiclesRequest request = new GetAllVehiclesRequest(activeOnly);
        GetAllVehiclesResponse response = mediator.dispatch(request);
        
        List<VehicleResponseDTO> responseDTOs = dtoMapper.toResponseDTOList(response.vehicles());
        
        return ResponseEntity.ok(responseDTOs);
    }
    
    @Operation(summary = "Get a vehicle by ID", description = "Get a vehicle by ID (ADMIN and OPERATOR)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        
        GetVehicleRequest request = new GetVehicleRequest(id);
        GetVehicleResponse response = mediator.dispatch(request);
        
        VehicleResponseDTO responseDTO = dtoMapper.toResponseDTO(response.vehicle());
        
        return ResponseEntity.ok(responseDTO);
    }
    
    @Operation(summary = "Find vehicle by license plate", description = "Find a vehicle by its license plate (ADMIN and OPERATOR)")
    @GetMapping("/by-plate/{licensePlate}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<VehicleResponseDTO> findVehicleByLicensePlate(@PathVariable String licensePlate) {
        
        FindVehicleByLicensePlateRequest request = new FindVehicleByLicensePlateRequest(licensePlate);
        FindVehicleByLicensePlateResponse response = mediator.dispatch(request);
        
        VehicleResponseDTO responseDTO = dtoMapper.toResponseDTO(response.vehicle());
        
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Search vehicle by license plate", description = "Search a vehicle by license plate using query parameter (ADMIN and OPERATOR)")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<VehicleResponseDTO> searchVehicleByLicensePlate(
            @Parameter(description = "License plate to search for") 
            @RequestParam String licensePlate) {
        
        FindVehicleByLicensePlateRequest request = new FindVehicleByLicensePlateRequest(licensePlate);
        FindVehicleByLicensePlateResponse response = mediator.dispatch(request);
        
        VehicleResponseDTO responseDTO = dtoMapper.toResponseDTO(response.vehicle());
        
        return ResponseEntity.ok(responseDTO);
    }
    
    @Operation(summary = "Change vehicle status", description = "Activate or deactivate a vehicle (ADMIN only)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> changeVehicleStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeVehicleStatusRequestDTO requestDTO) {
        
        ChangeVehicleStatusRequest request = new ChangeVehicleStatusRequest(id, requestDTO.isActive());
        mediator.dispatch(request);
        
        return ResponseEntity.noContent().build();
    }
}
