package io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.adapter;

import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.entity.ParkingSpaceEntity;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.mapper.ParkingSpaceMapper;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.infrastructure.repository.ParkingSpaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ParkingSpaceRepository using Spring Data JPA.
 * Adapts between domain and infrastructure layers.
 */
@Component
@RequiredArgsConstructor
public class ParkingSpaceRepositoryImpl implements ParkingSpaceRepository {
    
    private final ParkingSpaceJpaRepository jpaRepository;
    private final ParkingSpaceMapper mapper;
    
    @Override
    public ParkingSpace save(ParkingSpace parkingSpace) {
        ParkingSpaceEntity entity = mapper.toEntity(parkingSpace);
        ParkingSpaceEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<ParkingSpace> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<ParkingSpace> findBySpaceNumber(String spaceNumber) {
        return jpaRepository.findBySpaceNumber(spaceNumber)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<ParkingSpace> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSpace> findAllActive() {
        return jpaRepository.findAllByIsActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSpace> findAllAvailable() {
        return jpaRepository.findAllAvailable()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSpace> findAllAvailableByVehicleType(Long vehicleTypeId) {
        return jpaRepository.findAllAvailableByVehicleTypeId(vehicleTypeId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSpace> findAllOccupied() {
        return jpaRepository.findAllByIsOccupiedTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSpace> findAllByVehicleType(Long vehicleTypeId) {
        return jpaRepository.findAllByVehicleTypeId(vehicleTypeId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public Optional<ParkingSpace> findByOccupiedByVehiclePlate(String vehiclePlate) {
        return jpaRepository.findByOccupiedByVehiclePlate(vehiclePlate)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsBySpaceNumber(String spaceNumber) {
        return jpaRepository.existsBySpaceNumber(spaceNumber);
    }
    
    @Override
    public boolean existsBySpaceNumberAndIdNot(String spaceNumber, Long id) {
        return jpaRepository.existsBySpaceNumberAndIdNot(spaceNumber, id);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public long countAvailable() {
        return jpaRepository.countAvailable();
    }
    
    @Override
    public long countOccupied() {
        return jpaRepository.countByIsOccupiedTrue();
    }
    
    @Override
    public long countAvailableByVehicleType(Long vehicleTypeId) {
        return jpaRepository.countAvailableByVehicleTypeId(vehicleTypeId);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
