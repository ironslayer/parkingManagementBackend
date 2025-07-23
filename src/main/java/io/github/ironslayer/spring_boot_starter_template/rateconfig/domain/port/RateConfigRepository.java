package io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port;

import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio para el repositorio de configuraciones de tarifas
 * Define las operaciones que el dominio necesita sin depender de la infraestructura
 * 
 * Siguiendo los principios de la arquitectura hexagonal, este puerto permite
 * que el dominio defina qué necesita sin saber cómo se implementará.
 */
public interface RateConfigRepository {

    /**
     * Guarda o actualiza una configuración de tarifa
     * @param rateConfig la configuración a guardar
     * @return la configuración guardada con su ID asignado
     */
    RateConfig save(RateConfig rateConfig);

    /**
     * Busca una configuración de tarifa por su ID
     * @param id el ID de la configuración
     * @return Optional conteniendo la configuración si existe
     */
    Optional<RateConfig> findById(Long id);

    /**
     * Obtiene todas las configuraciones de tarifas
     * @return lista de todas las configuraciones
     */
    List<RateConfig> findAll();

    /**
     * Obtiene todas las configuraciones activas
     * @return lista de configuraciones activas ordenadas por tipo de vehículo
     */
    List<RateConfig> findAllActive();

    /**
     * Busca la configuración activa para un tipo de vehículo específico
     * @param vehicleTypeId el ID del tipo de vehículo
     * @return Optional conteniendo la configuración activa si existe
     */
    Optional<RateConfig> findActiveByVehicleTypeId(Long vehicleTypeId);

    /**
     * Verifica si existe una configuración activa para un tipo de vehículo
     * @param vehicleTypeId el ID del tipo de vehículo
     * @return true si existe una configuración activa
     */
    Boolean existsActiveByVehicleTypeId(Long vehicleTypeId);

    /**
     * Verifica si existe una configuración activa para un tipo de vehículo, 
     * excluyendo una configuración específica (útil para actualizaciones)
     * @param vehicleTypeId el ID del tipo de vehículo
     * @param excludeId el ID de la configuración a excluir
     * @return true si existe otra configuración activa
     */
    Boolean existsActiveByVehicleTypeIdAndIdNot(Long vehicleTypeId, Long excludeId);

    /**
     * Desactiva todas las configuraciones activas para un tipo de vehículo
     * Útil antes de activar una nueva configuración
     * @param vehicleTypeId el ID del tipo de vehículo
     */
    void deactivateAllByVehicleTypeId(Long vehicleTypeId);

    /**
     * Elimina una configuración de tarifa por su ID
     * @param id el ID de la configuración a eliminar
     */
    void deleteById(Long id);
}
