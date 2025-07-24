package io.github.ironslayer.spring_boot_starter_template.parkingspace.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ParkingSpace domain entity.
 * Defines the contract for data access operations following hexagonal architecture.
 */
public interface ParkingSpaceRepository {
    
    /**
     * Saves a new parking space or updates an existing one.
     */
    ParkingSpace save(ParkingSpace parkingSpace);
    
    /**
     * Finds a parking space by its ID.
     */
    Optional<ParkingSpace> findById(Long id);
    
    /**
     * Finds a parking space by its space number.
     */
    Optional<ParkingSpace> findBySpaceNumber(String spaceNumber);
    
    /**
     * Finds all parking spaces.
     */
    List<ParkingSpace> findAll();
    
    /**
     * Finds all active parking spaces.
     */
    List<ParkingSpace> findAllActive();
    
    /**
     * Finds all available parking spaces (active and not occupied).
     */
    List<ParkingSpace> findAllAvailable();
    
    /**
     * Finds all available parking spaces for a specific vehicle type.
     */
    List<ParkingSpace> findAllAvailableByVehicleType(Long vehicleTypeId);
    
    /**
     * Finds all occupied parking spaces.
     */
    List<ParkingSpace> findAllOccupied();
    
    /**
     * Finds all parking spaces by vehicle type.
     */
    List<ParkingSpace> findAllByVehicleType(Long vehicleTypeId);
    
    /**
     * Finds a parking space by the vehicle plate that is occupying it.
     */
    Optional<ParkingSpace> findByOccupiedByVehiclePlate(String vehiclePlate);
    
    /**
     * Checks if a space number already exists.
     */
    boolean existsBySpaceNumber(String spaceNumber);
    
    /**
     * Checks if a space number exists and is different from the given ID.
     * Useful for update operations to avoid duplicate space numbers.
     */
    boolean existsBySpaceNumberAndIdNot(String spaceNumber, Long id);
    
    /**
     * Counts the total number of parking spaces.
     */
    long count();
    
    /**
     * Counts the number of available parking spaces.
     */
    long countAvailable();
    
    /**
     * Counts the number of occupied parking spaces.
     */
    long countOccupied();
    
    /**
     * Counts the number of available parking spaces for a specific vehicle type.
     */
    long countAvailableByVehicleType(Long vehicleTypeId);
    
    /**
     * Deletes a parking space by ID.
     * Note: This should be used carefully and typically only when the space is not occupied.
     */
    void deleteById(Long id);
}
