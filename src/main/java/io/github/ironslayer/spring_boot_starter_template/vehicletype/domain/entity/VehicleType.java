package io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad de dominio para tipos de vehículos en el sistema de parqueo
 * - AUTO: Automóviles convencionales
 * - MOTO: Motocicletas y scooters  
 * - CAMIONETA: Camionetas y SUVs
 * - BICICLETA: Bicicletas (futuro)
 */
@Data
@Builder
public class VehicleType {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Valida que el nombre del tipo de vehículo sea válido
     */
    public boolean isValidName() {
        return name != null && 
               !name.trim().isEmpty() && 
               name.length() >= 2 && 
               name.length() <= 50;
    }
    
    /**
     * Activa el tipo de vehículo
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Desactiva el tipo de vehículo
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
}
