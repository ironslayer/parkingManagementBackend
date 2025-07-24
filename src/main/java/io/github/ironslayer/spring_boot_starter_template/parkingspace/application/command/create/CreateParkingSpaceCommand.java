package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.create;

/**
 * Command to create a new parking space.
 */
public record CreateParkingSpaceCommand(
        String spaceNumber,
        Long vehicleTypeId
) {
}
