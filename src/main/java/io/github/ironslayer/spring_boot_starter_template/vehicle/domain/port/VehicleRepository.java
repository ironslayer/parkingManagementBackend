package io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port;

import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio para la entidad Vehicle.
 * Define el contrato para las operaciones de acceso a datos siguiendo la arquitectura hexagonal.
 */
public interface VehicleRepository {
    
    /**
     * Guarda un nuevo vehículo o actualiza uno existente.
     */
    Vehicle save(Vehicle vehicle);
    
    /**
     * Busca un vehículo por su ID.
     */
    Optional<Vehicle> findById(Long id);
    
    /**
     * Busca un vehículo por su placa.
     */
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    
    /**
     * Busca todos los vehículos.
     */
    List<Vehicle> findAll();
    
    /**
     * Busca todos los vehículos activos.
     */
    List<Vehicle> findAllActive();
    
    /**
     * Busca vehículos por tipo de vehículo.
     */
    List<Vehicle> findByVehicleType(Long vehicleTypeId);
    
    /**
     * Busca vehículos activos por tipo de vehículo.
     */
    List<Vehicle> findActiveByVehicleType(Long vehicleTypeId);
    
    /**
     * Busca vehículos por propietario (nombre parcial).
     */
    List<Vehicle> findByOwnerNameContaining(String ownerName);
    
    /**
     * Busca vehículos por marca y modelo.
     */
    List<Vehicle> findByBrandAndModel(String brand, String model);
    
    /**
     * Verifica si existe un vehículo con la placa dada.
     */
    boolean existsByLicensePlate(String licensePlate);
    
    /**
     * Verifica si existe un vehículo con la placa dada excluyendo un ID específico.
     * Útil para operaciones de actualización.
     */
    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);
    
    /**
     * Cuenta el total de vehículos.
     */
    long count();
    
    /**
     * Cuenta vehículos activos.
     */
    long countActive();
    
    /**
     * Cuenta vehículos por tipo.
     */
    long countByVehicleType(Long vehicleTypeId);
    
    /**
     * Elimina un vehículo por ID (hard delete).
     * Nota: Se debe usar con cuidado, preferir desactivación.
     */
    void deleteById(Long id);
}
