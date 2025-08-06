package io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.changestatus;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import lombok.NonNull;

/**
 * Request para cambiar el estado de activación de un vehículo.
 * 
 * @param vehicleId ID del vehículo
 * @param isActive Nuevo estado del vehículo (true = activo, false = inactivo)
 */
public record ChangeVehicleStatusRequest(
        @NonNull Long vehicleId,
        @NonNull Boolean isActive
) implements Request<Void> {
}
