package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.entity.ParkingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para sesiones de parqueo.
 */
@Repository
public interface ParkingSessionJpaRepository extends JpaRepository<ParkingSessionEntity, Long> {
    
    /**
     * Encuentra la sesión activa de un vehículo específico
     */
    @Query("SELECT ps FROM ParkingSessionEntity ps WHERE ps.vehicleId = :vehicleId AND ps.isActive = true")
    Optional<ParkingSessionEntity> findActiveSessionByVehicleId(@Param("vehicleId") Long vehicleId);
    
    /**
     * Encuentra una sesión por código de ticket
     */
    @Query("SELECT ps FROM ParkingSessionEntity ps WHERE ps.ticketCode = :ticketCode")
    Optional<ParkingSessionEntity> findByTicketCode(@Param("ticketCode") String ticketCode);
    
    /**
     * Verifica si un vehículo tiene una sesión activa
     */
    @Query("SELECT COUNT(ps) > 0 FROM ParkingSessionEntity ps WHERE ps.vehicleId = :vehicleId AND ps.isActive = true")
    boolean hasActiveSession(@Param("vehicleId") Long vehicleId);
    
    /**
     * Obtiene todas las sesiones activas
     */
    @Query("SELECT ps FROM ParkingSessionEntity ps WHERE ps.isActive = true ORDER BY ps.entryTime DESC")
    List<ParkingSessionEntity> findAllActiveSessions();
    
    /**
     * Obtiene todas las sesiones de un vehículo específico
     */
    @Query("SELECT ps FROM ParkingSessionEntity ps WHERE ps.vehicleId = :vehicleId ORDER BY ps.entryTime DESC")
    List<ParkingSessionEntity> findSessionsByVehicleId(@Param("vehicleId") Long vehicleId);
    
    /**
     * Obtiene sesiones en un rango de fechas
     */
    @Query("SELECT ps FROM ParkingSessionEntity ps WHERE ps.entryTime >= :startDate AND ps.entryTime <= :endDate ORDER BY ps.entryTime DESC")
    List<ParkingSessionEntity> findSessionsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Obtiene sesiones por operador (entrada o salida)
     */
    @Query("SELECT ps FROM ParkingSessionEntity ps WHERE ps.operatorEntryId = :operatorId OR ps.operatorExitId = :operatorId ORDER BY ps.entryTime DESC")
    List<ParkingSessionEntity> findSessionsByOperatorId(@Param("operatorId") Long operatorId);
    
    /**
     * Cuenta sesiones activas
     */
    @Query("SELECT COUNT(ps) FROM ParkingSessionEntity ps WHERE ps.isActive = true")
    long countActiveSessions();
    
    /**
     * Obtiene sesiones por espacio de parqueo
     */
    @Query("SELECT ps FROM ParkingSessionEntity ps WHERE ps.parkingSpaceId = :parkingSpaceId ORDER BY ps.entryTime DESC")
    List<ParkingSessionEntity> findSessionsByParkingSpaceId(@Param("parkingSpaceId") Long parkingSpaceId);
}
