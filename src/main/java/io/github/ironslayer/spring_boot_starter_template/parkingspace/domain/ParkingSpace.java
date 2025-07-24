package io.github.ironslayer.spring_boot_starter_template.parkingspace.domain;

import java.time.LocalDateTime;

/**
 * Domain entity representing a parking space in the parking management system.
 * Follows hexagonal architecture principles with clean domain logic.
 */
public class ParkingSpace {
    
    private Long id;
    private String spaceNumber;
    private Long vehicleTypeId;
    private boolean isOccupied;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String occupiedByVehiclePlate;
    private LocalDateTime occupiedAt;

    // Default constructor
    public ParkingSpace() {
        this.isOccupied = false;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for creating new parking space
    public ParkingSpace(String spaceNumber, Long vehicleTypeId) {
        this();
        this.spaceNumber = spaceNumber;
        this.vehicleTypeId = vehicleTypeId;
    }

    // Full constructor
    public ParkingSpace(Long id, String spaceNumber, Long vehicleTypeId, 
                       boolean isOccupied, boolean isActive, LocalDateTime createdAt, 
                       LocalDateTime updatedAt, String occupiedByVehiclePlate, 
                       LocalDateTime occupiedAt) {
        this.id = id;
        this.spaceNumber = spaceNumber;
        this.vehicleTypeId = vehicleTypeId;
        this.isOccupied = isOccupied;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.occupiedByVehiclePlate = occupiedByVehiclePlate;
        this.occupiedAt = occupiedAt;
    }

    // Business logic methods
    
    /**
     * Checks if the parking space is available for occupation.
     * A space is available if it's active and not occupied.
     */
    public boolean isAvailable() {
        return isActive && !isOccupied;
    }

    /**
     * Occupies the parking space with the specified vehicle plate.
     * Validates that the space is available before occupation.
     */
    public void occupy(String vehiclePlate) {
        if (!isAvailable()) {
            throw new IllegalStateException("Parking space " + spaceNumber + " is not available for occupation");
        }
        
        if (vehiclePlate == null || vehiclePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle plate cannot be null or empty");
        }
        
        this.isOccupied = true;
        this.occupiedByVehiclePlate = vehiclePlate.toUpperCase().trim();
        this.occupiedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Frees the parking space, making it available for other vehicles.
     * Validates that the space is currently occupied.
     */
    public void free() {
        if (!isOccupied) {
            throw new IllegalStateException("Parking space " + spaceNumber + " is not currently occupied");
        }
        
        this.isOccupied = false;
        this.occupiedByVehiclePlate = null;
        this.occupiedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivates the parking space, making it unavailable for use.
     */
    public void deactivate() {
        if (isOccupied) {
            throw new IllegalStateException("Cannot deactivate occupied parking space " + spaceNumber);
        }
        
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activates the parking space, making it available for use.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the space information while preserving occupation state.
     */
    public void updateInfo(String spaceNumber, Long vehicleTypeId) {
        if (spaceNumber == null || spaceNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Space number cannot be null or empty");
        }
        
        if (vehicleTypeId == null) {
            throw new IllegalArgumentException("Vehicle type ID cannot be null");
        }
        
        this.spaceNumber = spaceNumber.toUpperCase().trim();
        this.vehicleTypeId = vehicleTypeId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Validates the space number format.
     * Space numbers should follow a pattern like "A001", "B102", etc.
     */
    public boolean isValidSpaceNumber() {
        if (spaceNumber == null || spaceNumber.trim().isEmpty()) {
            return false;
        }
        
        // Basic validation: should have at least one letter and one number
        String trimmed = spaceNumber.trim();
        return trimmed.length() >= 2 && 
               trimmed.matches("^[A-Z]+\\d+$");
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(String spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
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

    public String getOccupiedByVehiclePlate() {
        return occupiedByVehiclePlate;
    }

    public void setOccupiedByVehiclePlate(String occupiedByVehiclePlate) {
        this.occupiedByVehiclePlate = occupiedByVehiclePlate;
    }

    public LocalDateTime getOccupiedAt() {
        return occupiedAt;
    }

    public void setOccupiedAt(LocalDateTime occupiedAt) {
        this.occupiedAt = occupiedAt;
    }

    @Override
    public String toString() {
        return "ParkingSpace{" +
                "id=" + id +
                ", spaceNumber='" + spaceNumber + '\'' +
                ", vehicleTypeId=" + vehicleTypeId +
                ", isOccupied=" + isOccupied +
                ", isActive=" + isActive +
                ", occupiedByVehiclePlate='" + occupiedByVehiclePlate + '\'' +
                ", occupiedAt=" + occupiedAt +
                '}';
    }
}
