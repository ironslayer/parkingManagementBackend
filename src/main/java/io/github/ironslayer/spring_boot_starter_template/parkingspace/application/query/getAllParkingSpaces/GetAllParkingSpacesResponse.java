package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAllParkingSpaces;

import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;

import java.util.List;

/**
 * Response with all parking spaces.
 */
public record GetAllParkingSpacesResponse(
        List<ParkingSpace> parkingSpaces
) {
}
