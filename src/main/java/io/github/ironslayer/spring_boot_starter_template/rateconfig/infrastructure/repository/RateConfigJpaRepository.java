package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.entity.RateConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para RateConfigEntity
 * 
 * Incluye consultas personalizadas para:
 * - Buscar configuraciones activas
 * - Filtrar por tipo de vehículo
 * - Operaciones de desactivación masiva
 * - Validaciones de unicidad
 */
@Repository
public interface RateConfigJpaRepository extends JpaRepository<RateConfigEntity, Long> {

    /**
     * Buscar todas las configuraciones activas ordenadas por ID del tipo de vehículo
     */
    List<RateConfigEntity> findByIsActiveTrueOrderByVehicleTypeId();

    /**
     * Buscar la configuración activa para un tipo de vehículo específico
     */
    Optional<RateConfigEntity> findByVehicleTypeIdAndIsActiveTrue(Long vehicleTypeId);

    /**
     * Verificar si existe una configuración activa para un tipo de vehículo
     */
    boolean existsByVehicleTypeIdAndIsActiveTrue(Long vehicleTypeId);

    /**
     * Verificar si existe una configuración activa para un tipo de vehículo,
     * excluyendo una configuración específica (útil para actualizaciones)
     */
    @Query("SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END " +
           "FROM RateConfigEntity rc " +
           "WHERE rc.vehicleTypeId = :vehicleTypeId AND rc.isActive = true AND rc.id != :excludeId")
    boolean existsActiveByVehicleTypeIdAndIdNot(@Param("vehicleTypeId") Long vehicleTypeId, 
                                                @Param("excludeId") Long excludeId);

    /**
     * Obtener todas las configuraciones para un tipo de vehículo específico
     */
    List<RateConfigEntity> findByVehicleTypeIdOrderByCreatedAtDesc(Long vehicleTypeId);

    /**
     * Desactivar todas las configuraciones para un tipo de vehículo específico
     * Útil antes de activar una nueva configuración
     */
    @Modifying
    @Query("UPDATE RateConfigEntity rc SET rc.isActive = false WHERE rc.vehicleTypeId = :vehicleTypeId")
    void deactivateAllByVehicleTypeId(@Param("vehicleTypeId") Long vehicleTypeId);

    /**
     * Obtener configuraciones activas con información agregada
     */
    @Query("SELECT rc FROM RateConfigEntity rc WHERE rc.isActive = true ORDER BY rc.vehicleTypeId, rc.createdAt DESC")
    List<RateConfigEntity> findActiveConfigsOrdered();
}
