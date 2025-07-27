package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.createRateConfig.CreateRateConfigRequest;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.createRateConfig.CreateRateConfigResponse;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.command.updateRateConfig.UpdateRateConfigRequest;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getAllRateConfigs.GetAllRateConfigsRequest;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getAllRateConfigs.GetAllRateConfigsResponse;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfig.GetRateConfigRequest;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfig.GetRateConfigResponse;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfigByVehicleType.GetRateConfigByVehicleTypeRequest;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.application.query.getRateConfigByVehicleType.GetRateConfigByVehicleTypeResponse;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto.CreateRateConfigRequestDTO;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto.RateConfigResponseDTO;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.dto.RateConfigUpdateDTO;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.api.mapper.RateConfigDTOMapper;
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

/**
 * Controlador REST para gestión de configuraciones de tarifas
 * 
 * Endpoints implementados:
 * - GET /api/v1/rate-configs - Lista todas las configuraciones (ADMIN/OPERATOR)
 * - GET /api/v1/rate-configs/{id} - Obtiene una configuración por ID (ADMIN/OPERATOR)
 * - GET /api/v1/rate-configs/by-vehicle-type/{vehicleTypeId} - Obtiene configuración activa por tipo (ADMIN/OPERATOR)
 * - POST /api/v1/rate-configs - Crea nueva configuración (ADMIN)
 * - PATCH /api/v1/rate-configs/{id} - Actualiza configuración (ADMIN)
 */
@RequestMapping("/api/v1/rate-configs")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "RateConfig", description = "The Rate Configuration API. Contains all the operations that can be performed on parking rates.")
@RequiredArgsConstructor
@RestController
@Slf4j
public class RateConfigController {

    private final Mediator mediator;
    private final RateConfigDTOMapper dtoMapper;

    /**
     * Obtener una configuración de tarifa por ID
     */
    @Operation(summary = "Find a rate configuration", description = "Find a rate configuration by ID (ADMIN or OPERATOR)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<RateConfigResponseDTO> findById(@PathVariable Long id) {

        log.info("RateConfigController Get rate config by ID: {}", id);

        GetRateConfigResponse response = mediator.dispatch(new GetRateConfigRequest(id));

        RateConfigResponseDTO rateConfigDTO = dtoMapper.toResponseDTO(response);

        return ResponseEntity.ok(rateConfigDTO);
    }

    /**
     * Listar todas las configuraciones de tarifas
     */
    @Operation(summary = "List all rate configurations", description = "List all rate configurations (ADMIN only)")
    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RateConfigResponseDTO>> findAll() {

        log.info("RateConfigController Get all rate configurations");

        GetAllRateConfigsResponse response = mediator.dispatch(new GetAllRateConfigsRequest());

        List<RateConfigResponseDTO> rateConfigDTOs = dtoMapper.toResponseDTOList(response);

        return ResponseEntity.ok(rateConfigDTOs);
    }

    /**
     * Obtener configuración activa por tipo de vehículo
     */
    @Operation(summary = "Find active rate configuration by vehicle type", description = "Find the active rate configuration for a specific vehicle type (ADMIN or OPERATOR)")
    @GetMapping("/by-vehicle-type/{vehicleTypeId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<RateConfigResponseDTO> findByVehicleType(@PathVariable Long vehicleTypeId) {

        log.info("RateConfigController Get active rate config for vehicle type ID: {}", vehicleTypeId);

        GetRateConfigByVehicleTypeResponse response = mediator.dispatch(new GetRateConfigByVehicleTypeRequest(vehicleTypeId));

        RateConfigResponseDTO rateConfigDTO = dtoMapper.toResponseDTO(response);

        return ResponseEntity.ok(rateConfigDTO);
    }

    /**
     * Crear nueva configuración de tarifa
     */
    @Operation(summary = "Create rate configuration", description = "Create a new rate configuration (ADMIN only)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RateConfigResponseDTO> createRateConfig(@Valid @RequestBody CreateRateConfigRequestDTO request) {
        
        log.info("RateConfigController Creating rate config: {}", request);
        
        CreateRateConfigRequest command = dtoMapper.toCreateCommand(request);
        CreateRateConfigResponse createResponse = mediator.dispatch(command);
        
        // Obtener la entidad creada para la respuesta
        GetRateConfigRequest query = new GetRateConfigRequest(createResponse.id());
        GetRateConfigResponse rateConfigResponse = mediator.dispatch(query);
        
        RateConfigResponseDTO responseDTO = dtoMapper.toResponseDTO(rateConfigResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Actualizar configuración de tarifa existente
     */
    @Operation(summary = "Update a rate configuration", description = "Update a rate configuration (ADMIN only)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody RateConfigUpdateDTO updateDTO) {

        log.info("RateConfigController Updating rate config ID: {} with data: {}", id, updateDTO);

        // Crear el command con el ID del parámetro y los campos del body
        UpdateRateConfigRequest command = dtoMapper.toUpdateCommand(id, updateDTO);

        mediator.dispatch(command);

        return ResponseEntity.noContent().build();
    }
}
