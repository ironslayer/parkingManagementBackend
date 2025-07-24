package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.create;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;

/**
 * Request to create a new parking space.
 */
public record CreateParkingSpaceRequest(
        ParkingSpace parkingSpace
) implements Request<CreateParkingSpaceResponse> {
}
