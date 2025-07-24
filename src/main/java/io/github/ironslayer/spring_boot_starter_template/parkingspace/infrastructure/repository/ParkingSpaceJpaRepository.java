package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.entity.ParkingSpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ParkingSpaceEntity.
 */
@Repository
public interface ParkingSpaceJpaRepository extends JpaRepository<ParkingSpaceEntity, Long> {
    
    /**
     * Find parking space by space number.
     */
    Optional<ParkingSpaceEntity> findBySpaceNumber(String spaceNumber);
    
    /**
     * Find all active parking spaces.
     */
    List<ParkingSpaceEntity> findAllByIsActiveTrue();
    
    /**
     * Find all available parking spaces (active and not occupied).
     */
    @Query("SELECT p FROM ParkingSpaceEntity p WHERE p.isActive = true AND p.isOccupied = false")
    List<ParkingSpaceEntity> findAllAvailable();
    
    /**
     * Find all available parking spaces for a specific vehicle type.
     */
    @Query("SELECT p FROM ParkingSpaceEntity p WHERE p.isActive = true AND p.isOccupied = false AND p.vehicleTypeId = :vehicleTypeId")
    List<ParkingSpaceEntity> findAllAvailableByVehicleTypeId(@Param("vehicleTypeId") Long vehicleTypeId);
    
    /**
     * Find all occupied parking spaces.
     */
    List<ParkingSpaceEntity> findAllByIsOccupiedTrue();
    
    /**
     * Find all parking spaces by vehicle type.
     */
    List<ParkingSpaceEntity> findAllByVehicleTypeId(Long vehicleTypeId);
    
    /**
     * Find parking space by the vehicle plate that is occupying it.
     */
    Optional<ParkingSpaceEntity> findByOccupiedByVehiclePlate(String vehiclePlate);
    
    /**
     * Check if a space number already exists.
     */
    boolean existsBySpaceNumber(String spaceNumber);
    
    /**
     * Check if a space number exists and is different from the given ID.
     */
    boolean existsBySpaceNumberAndIdNot(String spaceNumber, Long id);
    
    /**
     * Count available parking spaces.
     */
    @Query("SELECT COUNT(p) FROM ParkingSpaceEntity p WHERE p.isActive = true AND p.isOccupied = false")
    long countAvailable();
    
    /**
     * Count occupied parking spaces.
     */
    long countByIsOccupiedTrue();
    
    /**
     * Count available parking spaces for a specific vehicle type.
     */
    @Query("SELECT COUNT(p) FROM ParkingSpaceEntity p WHERE p.isActive = true AND p.isOccupied = false AND p.vehicleTypeId = :vehicleTypeId")
    long countAvailableByVehicleTypeId(@Param("vehicleTypeId") Long vehicleTypeId);
}
