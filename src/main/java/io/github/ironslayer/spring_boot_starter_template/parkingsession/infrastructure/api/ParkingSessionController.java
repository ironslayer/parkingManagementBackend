package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession.EndSessionRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession.EndSessionResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession.StartSessionRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession.StartSessionResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getActiveSessions.GetActiveSessionsRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getActiveSessions.GetActiveSessionsResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getSessionByVehicle.GetSessionByVehicleRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getSessionByVehicle.GetSessionByVehicleResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.EndSessionRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.EndSessionResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.StartSessionRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.dto.StartSessionResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.mapper.ParkingSessionDTOMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller para operaciones de sesiones de parqueo.
 * Proporciona endpoints para entrada, salida y consulta de sesiones.
 */
@RestController
@RequestMapping("/api/v1/parking-sessions")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Parking Sessions", description = "The Parking Session API. Contains all operations for managing parking sessions (entry, exit, queries).")
@RequiredArgsConstructor
@Slf4j
public class ParkingSessionController {
    
    private final Mediator mediator;
    private final ParkingSessionDTOMapper dtoMapper;
    
    @Operation(summary = "Start a parking session", description = "Register vehicle entry to parking lot (ADMIN and OPERATOR)")
    @PostMapping("/start")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<StartSessionResponseDTO> startSession(
            @Valid @RequestBody StartSessionRequestDTO requestDTO) {
        
        log.info("=== CONTROLLER: Starting parking session endpoint called ===");
        log.info("CONTROLLER: Request DTO: {}", requestDTO);
        
        try {
            StartSessionRequest request = dtoMapper.toStartSessionRequest(requestDTO);
            log.info("CONTROLLER: Mapped to request: {}", request);
            
            StartSessionResponse response = mediator.dispatch(request);
            log.info("CONTROLLER: Handler response: {}", response);
            
            StartSessionResponseDTO responseDTO = dtoMapper.toStartSessionResponseDTO(response);
            log.info("CONTROLLER: Mapped to response DTO: {}", responseDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("CONTROLLER: Error processing request", e);
            throw e;
        }
    }
    
    @Operation(summary = "End a parking session", description = "Register vehicle exit from parking lot and calculate payment (ADMIN and OPERATOR)")
    @PostMapping("/end")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<EndSessionResponseDTO> endSession(
            @Valid @RequestBody EndSessionRequestDTO requestDTO) {
        
        EndSessionRequest request = dtoMapper.toEndSessionRequest(requestDTO);
        EndSessionResponse response = mediator.dispatch(request);
        
        EndSessionResponseDTO responseDTO = dtoMapper.toEndSessionResponseDTO(response);
        
        return ResponseEntity.ok(responseDTO);
    }
    
    @Operation(summary = "Get all active parking sessions", description = "Get all currently active parking sessions (ADMIN and OPERATOR)")
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<GetActiveSessionsResponse> getActiveSessions() {
        
        GetActiveSessionsRequest request = new GetActiveSessionsRequest();
        GetActiveSessionsResponse response = mediator.dispatch(request);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get parking session by vehicle", description = "Get active parking session for a specific vehicle (ADMIN and OPERATOR)")
    @GetMapping("/vehicle/{licensePlate}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<GetSessionByVehicleResponse> getSessionByVehicle(
            @Parameter(description = "Vehicle license plate")
            @PathVariable String licensePlate) {
        
        GetSessionByVehicleRequest request = new GetSessionByVehicleRequest(licensePlate);
        GetSessionByVehicleResponse response = mediator.dispatch(request);
        
        return ResponseEntity.ok(response);
    }
}
