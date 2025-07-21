package io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.vehicletype.infrastructure.entity.VehicleTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para VehicleTypeEntity
 */
@Repository
public interface VehicleTypeJpaRepository extends JpaRepository<VehicleTypeEntity, Long> {

    /**
     * Buscar tipo de vehículo por nombre
     */
    Optional<VehicleTypeEntity> findByNameIgnoreCase(String name);

    /**
     * Buscar tipos de vehículos activos
     */
    List<VehicleTypeEntity> findByIsActiveTrueOrderByName();

    /**
     * Verificar si existe un tipo de vehículo con el nombre dado (excluyendo el ID actual)
     */
    @Query("SELECT CASE WHEN COUNT(vt) > 0 THEN true ELSE false END " +
           "FROM VehicleTypeEntity vt " +
           "WHERE LOWER(vt.name) = LOWER(?1) AND (?2 IS NULL OR vt.id != ?2)")
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long excludeId);

    /**
     * Verificar si existe un tipo de vehículo activo con el nombre dado
     */
    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
}
