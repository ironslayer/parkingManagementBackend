package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA entity for parking spaces.
 * Maps to the 'parking_spaces' table in the database.
 */
@Entity
@Table(name = "parking_spaces")
public class ParkingSpaceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "space_number", nullable = false, unique = true, length = 10)
    private String spaceNumber;
    
    @Column(name = "vehicle_type_id", nullable = false)
    private Long vehicleTypeId;
    
    @Column(name = "is_occupied", nullable = false)
    private Boolean isOccupied = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "occupied_by_vehicle_plate", length = 15)
    private String occupiedByVehiclePlate;
    
    @Column(name = "occupied_at")
    private LocalDateTime occupiedAt;

    // Default constructor
    public ParkingSpaceEntity() {
    }

    // Constructor with required fields
    public ParkingSpaceEntity(String spaceNumber, Long vehicleTypeId) {
        this.spaceNumber = spaceNumber;
        this.vehicleTypeId = vehicleTypeId;
        this.isOccupied = false;
        this.isActive = true;
    }

    // Full constructor
    public ParkingSpaceEntity(Long id, String spaceNumber, Long vehicleTypeId, 
                             Boolean isOccupied, Boolean isActive, LocalDateTime createdAt, 
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

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean occupied) {
        isOccupied = occupied;
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
        return "ParkingSpaceEntity{" +
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
