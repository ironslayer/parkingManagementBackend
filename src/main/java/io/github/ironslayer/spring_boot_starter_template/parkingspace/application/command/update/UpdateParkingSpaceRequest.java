package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.update;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;

/**
 * Request to update an existing parking space.
 */
public record UpdateParkingSpaceRequest(
        ParkingSpace parkingSpace
) implements Request<Void> {
}
