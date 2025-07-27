package io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port;

import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto para el repositorio de sesiones de parqueo.
 * Define las operaciones de persistencia necesarias para el dominio.
 */
public interface ParkingSessionRepository {
    
    /**
     * Guarda una nueva sesión de parqueo o actualiza una existente
     */
    ParkingSession save(ParkingSession parkingSession);
    
    /**
     * Busca una sesión por su ID
     */
    Optional<ParkingSession> findById(Long id);
    
    /**
     * Busca la sesión activa de un vehículo específico
     * @param vehicleId ID del vehículo
     * @return Sesión activa si existe
     */
    Optional<ParkingSession> findActiveSessionByVehicleId(Long vehicleId);
    
    /**
     * Busca una sesión por código de ticket
     * @param ticketCode Código del ticket
     * @return Sesión si existe
     */
    Optional<ParkingSession> findByTicketCode(String ticketCode);
    
    /**
     * Verifica si un vehículo tiene una sesión activa
     */
    boolean hasActiveSession(Long vehicleId);
    
    /**
     * Obtiene todas las sesiones activas
     */
    List<ParkingSession> findAllActiveSessions();
    
    /**
     * Obtiene todas las sesiones de un vehículo específico
     * @param vehicleId ID del vehículo
     * @return Lista de sesiones ordenadas por fecha de entrada (más reciente primero)
     */
    List<ParkingSession> findSessionsByVehicleId(Long vehicleId);
    
    /**
     * Obtiene sesiones en un rango de fechas
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de sesiones en el rango especificado
     */
    List<ParkingSession> findSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Obtiene sesiones por operador
     * @param operatorId ID del operador
     * @return Lista de sesiones donde el operador participó (entrada o salida)
     */
    List<ParkingSession> findSessionsByOperatorId(Long operatorId);
    
    /**
     * Obtiene el conteo de sesiones activas
     */
    long countActiveSessions();
    
    /**
     * Obtiene sesiones por espacio de parqueo
     * @param parkingSpaceId ID del espacio de parqueo
     * @return Lista de sesiones para ese espacio
     */
    List<ParkingSession> findSessionsByParkingSpaceId(Long parkingSpaceId);
    
    /**
     * Verifica si existe una sesión por ID
     */
    boolean existsById(Long id);
}
