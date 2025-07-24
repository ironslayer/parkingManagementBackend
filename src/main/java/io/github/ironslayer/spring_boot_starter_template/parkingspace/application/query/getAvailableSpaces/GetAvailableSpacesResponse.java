package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAvailableSpaces;

import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;

import java.util.List;

/**
 * Response with available parking spaces.
 */
public record GetAvailableSpacesResponse(
        List<ParkingSpace> availableSpaces,
        long totalCount
) {
}
