package io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear un nuevo tipo de vehículo
 */
public class CreateVehicleTypeRequestDTO {

    @NotBlank(message = "Vehicle type name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @JsonProperty("name")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    @JsonProperty("description")
    private String description;

    // Constructores
    public CreateVehicleTypeRequestDTO() {}

    public CreateVehicleTypeRequestDTO(String name, String description) {
        this.name = name;
        this.description = description;
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

    @Override
    public String toString() {
        return "CreateVehicleTypeRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
