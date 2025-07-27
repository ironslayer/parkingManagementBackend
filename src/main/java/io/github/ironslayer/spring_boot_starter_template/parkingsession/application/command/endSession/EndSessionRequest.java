package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;

/**
 * Request para finalizar una sesión de parqueo.
 * Se puede finalizar con la placa del vehículo, ID de la sesión o código del ticket.
 * Múltiples opciones son válidas aquí porque el operador puede tener diferentes formas
 * de identificar la sesión a finalizar (ticket físico, placa, etc.)
 */
public record EndSessionRequest(
    String licensePlate,
    Long sessionId,
    String ticketCode,
    Long operatorId
) implements Request<EndSessionResponse> {
    
    // Constructor para finalizar con placa (más común)
    public EndSessionRequest(String licensePlate, Long operatorId) {
        this(licensePlate, null, null, operatorId);
    }
    
    // Constructor para finalizar con código de ticket
    public static EndSessionRequest byTicketCode(String ticketCode, Long operatorId) {
        return new EndSessionRequest(null, null, ticketCode, operatorId);
    }
    
    // Constructor para finalizar con ID de sesión (menos común)
    public static EndSessionRequest bySessionId(Long sessionId, Long operatorId) {
        return new EndSessionRequest(null, sessionId, null, operatorId);
    }
}
