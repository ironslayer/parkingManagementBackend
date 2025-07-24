package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAvailableSpaces;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request to get available parking spaces, optionally filtered by vehicle type.
 */
public record GetAvailableSpacesRequest(
        Long vehicleTypeId // Optional: null to get all available spaces
) implements Request<GetAvailableSpacesResponse> {
}
