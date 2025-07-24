package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.create;

/**
 * Response from creating a parking space.
 */
public record CreateParkingSpaceResponse(
        Long parkingSpaceId
) {
}
