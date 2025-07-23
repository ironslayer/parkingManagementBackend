package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.entity.RateConfigEntity;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.mapper.RateConfigMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del puerto RateConfigRepository usando Spring Data JPA
 * 
 * Actúa como adaptador entre la capa de dominio y la infraestructura de persistencia,
 * delegando las operaciones al JpaRepository y usando el mapper para las conversiones
 */
@Repository
public class RateConfigRepositoryImpl implements RateConfigRepository {

    private final RateConfigJpaRepository jpaRepository;
    private final RateConfigMapper mapper;

    public RateConfigRepositoryImpl(RateConfigJpaRepository jpaRepository, RateConfigMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RateConfig save(RateConfig rateConfig) {
        RateConfigEntity entity = mapper.toEntity(rateConfig);
        RateConfigEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RateConfig> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<RateConfig> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<RateConfig> findAllActive() {
        return jpaRepository.findByIsActiveTrueOrderByVehicleTypeId()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<RateConfig> findActiveByVehicleTypeId(Long vehicleTypeId) {
        return jpaRepository.findByVehicleTypeIdAndIsActiveTrue(vehicleTypeId)
                .map(mapper::toDomain);
    }

    @Override
    public Boolean existsActiveByVehicleTypeId(Long vehicleTypeId) {
        return jpaRepository.existsByVehicleTypeIdAndIsActiveTrue(vehicleTypeId);
    }

    @Override
    public Boolean existsActiveByVehicleTypeIdAndIdNot(Long vehicleTypeId, Long excludeId) {
        return jpaRepository.existsActiveByVehicleTypeIdAndIdNot(vehicleTypeId, excludeId);
    }

    @Override
    @Transactional
    public void deactivateAllByVehicleTypeId(Long vehicleTypeId) {
        jpaRepository.deactivateAllByVehicleTypeId(vehicleTypeId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
