package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.query.getAllParkingSpaces;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request to get all parking spaces.
 */
public record GetAllParkingSpacesRequest() implements Request<GetAllParkingSpacesResponse> {
}
