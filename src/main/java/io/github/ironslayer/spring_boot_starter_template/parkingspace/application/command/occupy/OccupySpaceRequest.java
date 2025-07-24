package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.occupy;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request to occupy a parking space with a vehicle.
 */
public record OccupySpaceRequest(
        Long parkingSpaceId,
        String vehiclePlate
) implements Request<Void> {
}
