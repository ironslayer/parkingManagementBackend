package io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.entity.VehicleTypeEntity;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.mapper.VehicleTypeMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del puerto VehicleTypeRepository usando Spring Data JPA
 */
@Repository
public class VehicleTypeRepositoryImpl implements VehicleTypeRepository {

    private final VehicleTypeJpaRepository jpaRepository;
    private final VehicleTypeMapper mapper;

    public VehicleTypeRepositoryImpl(VehicleTypeJpaRepository jpaRepository, VehicleTypeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public VehicleType save(VehicleType vehicleType) {
        VehicleTypeEntity entity = mapper.toEntity(vehicleType);
        VehicleTypeEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VehicleType> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<VehicleType> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<VehicleType> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<VehicleType> findAllActive() {
        return jpaRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCaseAndIsActiveTrue(name);
    }

    public boolean existsByNameAndIdNot(String name, Long excludeId) {
        return jpaRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
