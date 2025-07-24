package io.github.ironslayer.spring_boot_starter_template.parkingspace.application.command.free;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request to free a parking space.
 */
public record FreeSpaceRequest(
        Long parkingSpaceId
) implements Request<Void> {
}
