package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para vehículos.
 * Mapea a la tabla 'vehicles' en la base de datos.
 */
@Entity
@Table(name = "vehicles")
public class VehicleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "license_plate", nullable = false, unique = true, length = 15)
    private String licensePlate;
    
    @Column(name = "vehicle_type_id", nullable = false)
    private Long vehicleTypeId;
    
    @Column(name = "brand", length = 50)
    private String brand;
    
    @Column(name = "model", length = 50)
    private String model;
    
    @Column(name = "color", length = 30)
    private String color;
    
    @Column(name = "owner_name", length = 100)
    private String ownerName;
    
    @Column(name = "owner_phone", length = 20)
    private String ownerPhone;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor por defecto
    public VehicleEntity() {
    }

    // Constructor con campos requeridos
    public VehicleEntity(String licensePlate, Long vehicleTypeId) {
        this.licensePlate = licensePlate;
        this.vehicleTypeId = vehicleTypeId;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor completo
    public VehicleEntity(Long id, String licensePlate, Long vehicleTypeId, String brand, 
                        String model, String color, String ownerName, String ownerPhone, 
                        Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.vehicleTypeId = vehicleTypeId;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
