package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getActiveSessions;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para obtener todas las sesiones activas de parqueo.
 */
public record GetActiveSessionsRequest() implements Request<GetActiveSessionsResponse> {
}
