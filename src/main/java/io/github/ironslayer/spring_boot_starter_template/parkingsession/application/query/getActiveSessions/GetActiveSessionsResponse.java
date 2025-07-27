package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getActiveSessions;

import java.util.List;

/**
 * Response con todas las sesiones activas.
 */
public record GetActiveSessionsResponse(
    List<ActiveSessionDTO> activeSessions,
    int totalCount
) {
    /**
     * DTO para mostrar información resumida de una sesión activa
     */
    public record ActiveSessionDTO(
        Long sessionId,
        String licensePlate,
        String vehicleType,
        String parkingSpace,
        String entryTime,
        String hoursParked,
        String operatorName
    ) {}
}
