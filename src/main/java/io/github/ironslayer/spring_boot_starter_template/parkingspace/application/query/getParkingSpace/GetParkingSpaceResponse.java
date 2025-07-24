package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getParkingSpace;

import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;

/**
 * Response with a parking space.
 */
public record GetParkingSpaceResponse(
        ParkingSpace parkingSpace
) {
}
