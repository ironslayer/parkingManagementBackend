package io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port;

import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio para el repositorio de tipos de vehículos
 * Define las operaciones que el dominio necesita sin depender de la infraestructura
 */
public interface VehicleTypeRepository {

    VehicleType save(VehicleType vehicleType);

    Optional<VehicleType> findById(Long id);

    List<VehicleType> findAll();

    List<VehicleType> findAllActive();

    Optional<VehicleType> findByName(String name);

    Boolean existsByName(String name);

    void deleteById(Long id);
}
