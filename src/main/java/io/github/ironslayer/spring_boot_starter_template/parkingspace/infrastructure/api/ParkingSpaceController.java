package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.create.CreateParkingSpaceRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.create.CreateParkingSpaceResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.free.FreeSpaceRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.occupy.OccupySpaceRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.update.UpdateParkingSpaceRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAllParkingSpaces.GetAllParkingSpacesRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAllParkingSpaces.GetAllParkingSpacesResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAvailableSpaces.GetAvailableSpacesRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAvailableSpaces.GetAvailableSpacesResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getParkingSpace.GetParkingSpaceRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getParkingSpace.GetParkingSpaceResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.dto.*;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.api.mapper.ParkingSpaceDTOMapper;
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
 * REST Controller for parking space management operations.
 * Provides endpoints for CRUD operations and space availability management.
 */
@RestController
@RequestMapping("/api/v1/parking-spaces")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Parking Spaces", description = "The Parking Space API. Contains all operations for managing parking spaces.")
@RequiredArgsConstructor
public class ParkingSpaceController {
    
    private final Mediator mediator;
    private final ParkingSpaceDTOMapper dtoMapper;
    
    @Operation(summary = "Create a new parking space", description = "Create a new parking space (ADMIN only)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParkingSpaceResponseDTO> createParkingSpace(
            @Valid @RequestBody CreateParkingSpaceRequestDTO requestDTO) {
        
        ParkingSpace parkingSpace = dtoMapper.toDomain(requestDTO);
        CreateParkingSpaceRequest request = new CreateParkingSpaceRequest(parkingSpace);
        CreateParkingSpaceResponse response = mediator.dispatch(request);
        
        // Get the created parking space to return full information
        GetParkingSpaceRequest getRequest = new GetParkingSpaceRequest(response.parkingSpaceId());
        GetParkingSpaceResponse getResponse = mediator.dispatch(getRequest);
        
        ParkingSpaceResponseDTO responseDTO = dtoMapper.toResponseDTO(getResponse.parkingSpace());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @Operation(summary = "Update an existing parking space", description = "Partially update an existing parking space (ADMIN only)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateParkingSpace(
            @PathVariable Long id,
            @Valid @RequestBody UpdateParkingSpaceRequestDTO requestDTO) {
        
        // Create domain entity with ID from path parameter
        ParkingSpace parkingSpace = dtoMapper.toDomain(requestDTO);
        parkingSpace.setId(id); // Set ID from URL path
        
        UpdateParkingSpaceRequest request = new UpdateParkingSpaceRequest(parkingSpace);
        mediator.dispatch(request);
        
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all parking spaces", description = "Get all parking spaces (ADMIN and OPERATOR)")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<ParkingSpaceResponseDTO>> getAllParkingSpaces() {
        
        GetAllParkingSpacesRequest request = new GetAllParkingSpacesRequest();
        GetAllParkingSpacesResponse response = mediator.dispatch(request);
        
        List<ParkingSpaceResponseDTO> responseDTOs = dtoMapper.toResponseDTOList(response.parkingSpaces());
        
        return ResponseEntity.ok(responseDTOs);
    }
    
    @Operation(summary = "Get a parking space by ID", description = "Get a parking space by ID (ADMIN and OPERATOR)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<ParkingSpaceResponseDTO> getParkingSpaceById(@PathVariable Long id) {
        
        GetParkingSpaceRequest request = new GetParkingSpaceRequest(id);
        GetParkingSpaceResponse response = mediator.dispatch(request);
        
        ParkingSpaceResponseDTO responseDTO = dtoMapper.toResponseDTO(response.parkingSpace());
        
        return ResponseEntity.ok(responseDTO);
    }
    
    @Operation(summary = "Get available parking spaces", 
              description = "Get available parking spaces, optionally filtered by vehicle type (ADMIN and OPERATOR)")
    @GetMapping("/available")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<AvailableSpacesResponseDTO> getAvailableSpaces(
            @Parameter(description = "Optional vehicle type ID to filter by")
            @RequestParam(required = false) Long vehicleTypeId) {
        
        GetAvailableSpacesRequest request = new GetAvailableSpacesRequest(vehicleTypeId);
        GetAvailableSpacesResponse response = mediator.dispatch(request);
        
        AvailableSpacesResponseDTO responseDTO = dtoMapper.toAvailableSpacesResponseDTO(
                response.availableSpaces(), response.totalCount(), vehicleTypeId);
        
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get real-time parking spaces status", 
              description = "Get current status of all parking spaces with real-time occupancy information (ADMIN and OPERATOR)")
    @GetMapping("/status")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<List<ParkingSpaceResponseDTO>> getParkingSpacesStatus() {
        
        GetAllParkingSpacesRequest request = new GetAllParkingSpacesRequest();
        GetAllParkingSpacesResponse response = mediator.dispatch(request);
        
        List<ParkingSpaceResponseDTO> responseDTO = response.parkingSpaces()
                .stream()
                .map(dtoMapper::toResponseDTO)
                .toList();
        
        return ResponseEntity.ok(responseDTO);
    }
    
    @Operation(summary = "Occupy a parking space", description = "Occupy a parking space with a vehicle (ADMIN and OPERATOR)")
    @PostMapping("/{id}/occupy")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<Void> occupyParkingSpace(
            @PathVariable Long id,
            @Valid @RequestBody OccupySpaceRequestDTO requestDTO) {
        
        OccupySpaceRequest request = new OccupySpaceRequest(id, requestDTO.vehiclePlate());
        mediator.dispatch(request);
        
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "Free a parking space", description = "Free a parking space (ADMIN and OPERATOR)")
    @PostMapping("/{id}/free")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<Void> freeParkingSpace(@PathVariable Long id) {
        
        FreeSpaceRequest request = new FreeSpaceRequest(id);
        mediator.dispatch(request);
        
        return ResponseEntity.ok().build();
    }
}
