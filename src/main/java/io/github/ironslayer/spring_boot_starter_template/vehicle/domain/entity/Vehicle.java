package io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity;

import java.time.LocalDateTime;

/**
 * Entidad de dominio para vehículos en el sistema de parqueo.
 * Representa un vehículo registrado con toda su información básica.
 * 
 * Lógica de negocio incluida:
 * - Validación de formato de placa
 * - Normalización de datos
 * - Gestión de estado activo/inactivo
 */
public class Vehicle {
    
    private Long id;
    private String licensePlate;
    private Long vehicleTypeId;
    private String brand;
    private String model;
    private String color;
    private String ownerName;
    private String ownerPhone;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor por defecto
    public Vehicle() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor para crear nuevo vehículo
    public Vehicle(String licensePlate, Long vehicleTypeId, String brand, 
                   String model, String color, String ownerName, String ownerPhone) {
        this();
        this.licensePlate = normalizeLicensePlate(licensePlate);
        this.vehicleTypeId = vehicleTypeId;
        this.brand = normalizeText(brand);
        this.model = normalizeText(model);
        this.color = normalizeText(color);
        this.ownerName = normalizeText(ownerName);
        this.ownerPhone = normalizePhone(ownerPhone);
    }

    // Constructor completo
    public Vehicle(Long id, String licensePlate, Long vehicleTypeId, String brand, 
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

    // Métodos de lógica de negocio

    /**
     * Valida el formato de la placa del vehículo.
     * Debe tener entre 3 y 15 caracteres alfanuméricos.
     */
    public boolean isValidLicensePlate() {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = licensePlate.trim();
        return trimmed.length() >= 3 && 
               trimmed.length() <= 15 && 
               trimmed.matches("^[A-Z0-9\\-]+$");
    }

    /**
     * Actualiza la información del vehículo manteniendo la placa y timestamps.
     */
    public void updateInfo(Long vehicleTypeId, String brand, String model, 
                          String color, String ownerName, String ownerPhone) {
        if (vehicleTypeId == null) {
            throw new IllegalArgumentException("Vehicle type ID cannot be null");
        }
        
        this.vehicleTypeId = vehicleTypeId;
        this.brand = normalizeText(brand);
        this.model = normalizeText(model);
        this.color = normalizeText(color);
        this.ownerName = normalizeText(ownerName);
        this.ownerPhone = normalizePhone(ownerPhone);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza solo la información del propietario.
     */
    public void updateOwnerInfo(String ownerName, String ownerPhone) {
        this.ownerName = normalizeText(ownerName);
        this.ownerPhone = normalizePhone(ownerPhone);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Desactiva el vehículo (soft delete).
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reactiva el vehículo.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el vehículo está activo.
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    // Métodos privados de normalización

    private String normalizeLicensePlate(String plate) {
        if (plate == null) {
            throw new IllegalArgumentException("License plate cannot be null");
        }
        return plate.toUpperCase().trim().replaceAll("\\s+", "");
    }

    private String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return text.trim();
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        // Eliminar espacios y caracteres especiales, mantener solo números, + y -
        return phone.trim().replaceAll("[^\\d+\\-]", "");
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

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", licensePlate='" + licensePlate + '\'' +
                ", vehicleTypeId=" + vehicleTypeId +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
