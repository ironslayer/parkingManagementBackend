package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession;

/**
 * Response del comando de iniciar sesión de parqueo.
 * Contiene la información del ticket generado para el cliente.
 */
public record StartSessionResponse(
    Long sessionId,
    String ticketCode,
    String licensePlate,
    String vehicleType,
    String assignedSpace,
    String entryTime,
    String operatorName
) {}
