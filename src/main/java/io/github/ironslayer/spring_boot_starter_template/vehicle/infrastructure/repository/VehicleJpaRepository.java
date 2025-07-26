package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository para VehicleEntity.
 */
@Repository
public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, Long> {
    
    /**
     * Busca un vehículo por placa.
     */
    Optional<VehicleEntity> findByLicensePlate(String licensePlate);
    
    /**
     * Busca todos los vehículos activos.
     */
    List<VehicleEntity> findByIsActiveTrue();
    
    /**
     * Busca vehículos por tipo de vehículo.
     */
    List<VehicleEntity> findByVehicleTypeId(Long vehicleTypeId);
    
    /**
     * Busca vehículos activos por tipo de vehículo.
     */
    List<VehicleEntity> findByVehicleTypeIdAndIsActiveTrue(Long vehicleTypeId);
    
    /**
     * Busca vehículos por propietario (nombre que contenga).
     */
    List<VehicleEntity> findByOwnerNameContainingIgnoreCase(String ownerName);
    
    /**
     * Busca vehículos por marca y modelo.
     */
    List<VehicleEntity> findByBrandAndModel(String brand, String model);
    
    /**
     * Verifica si existe un vehículo con la placa dada.
     */
    boolean existsByLicensePlate(String licensePlate);
    
    /**
     * Verifica si existe un vehículo con la placa dada excluyendo un ID específico.
     */
    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);
    
    /**
     * Cuenta vehículos activos.
     */
    long countByIsActiveTrue();
    
    /**
     * Cuenta vehículos por tipo.
     */
    long countByVehicleTypeId(Long vehicleTypeId);
}
