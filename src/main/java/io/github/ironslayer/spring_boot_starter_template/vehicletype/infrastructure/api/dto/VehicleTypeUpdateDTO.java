package io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar un tipo de vehículo (actualización parcial)
 * Solo contiene los campos que se pueden modificar, todos opcionales
 */
public class VehicleTypeUpdateDTO {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @JsonProperty("name")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    @JsonProperty("description")
    private String description;

    @JsonProperty("isActive")
    private Boolean isActive;

    // Constructor vacío
    public VehicleTypeUpdateDTO() {}

    // Constructor completo
    public VehicleTypeUpdateDTO(String name, String description, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "VehicleTypeUpdateDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
