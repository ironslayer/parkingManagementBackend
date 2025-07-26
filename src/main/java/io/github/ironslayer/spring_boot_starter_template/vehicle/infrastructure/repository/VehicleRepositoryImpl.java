package io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.entity.VehicleEntity;
import io.github.ironslayer.spring_boot_starter_template.vehicle.infrastructure.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del puerto VehicleRepository usando JPA.
 * Adapta las operaciones de dominio a las operaciones de persistencia.
 */
@Repository
@RequiredArgsConstructor
public class VehicleRepositoryImpl implements VehicleRepository {
    
    private final VehicleJpaRepository jpaRepository;
    private final VehicleMapper mapper;
    
    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = mapper.toEntity(vehicle);
        VehicleEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Vehicle> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        return jpaRepository.findByLicensePlate(licensePlate)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Vehicle> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicle> findAllActive() {
        return jpaRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicle> findByVehicleType(Long vehicleTypeId) {
        return jpaRepository.findByVehicleTypeId(vehicleTypeId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicle> findActiveByVehicleType(Long vehicleTypeId) {
        return jpaRepository.findByVehicleTypeIdAndIsActiveTrue(vehicleTypeId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicle> findByOwnerNameContaining(String ownerName) {
        return jpaRepository.findByOwnerNameContainingIgnoreCase(ownerName)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicle> findByBrandAndModel(String brand, String model) {
        return jpaRepository.findByBrandAndModel(brand, model)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return jpaRepository.existsByLicensePlate(licensePlate);
    }
    
    @Override
    public boolean existsByLicensePlateAndIdNot(String licensePlate, Long id) {
        return jpaRepository.existsByLicensePlateAndIdNot(licensePlate, id);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public long countActive() {
        return jpaRepository.countByIsActiveTrue();
    }
    
    @Override
    public long countByVehicleType(Long vehicleTypeId) {
        return jpaRepository.countByVehicleTypeId(vehicleTypeId);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
