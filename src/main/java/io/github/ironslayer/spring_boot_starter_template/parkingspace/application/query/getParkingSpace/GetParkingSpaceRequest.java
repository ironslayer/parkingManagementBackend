package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getParkingSpace;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request to get a parking space by ID.
 */
public record GetParkingSpaceRequest(
        Long parkingSpaceId
) implements Request<GetParkingSpaceResponse> {
}
