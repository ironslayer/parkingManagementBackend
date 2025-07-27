package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Entidad JPA para sesiones de parqueo.
 * Mapea la tabla parking_sessions en la base de datos.
 */
@Entity
@Table(name = "parking_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSessionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;
    
    @Column(name = "parking_space_id", nullable = false)
    private Long parkingSpaceId;
    
    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;
    
    @Column(name = "exit_time")
    private LocalDateTime exitTime;
    
    @Column(name = "operator_entry_id", nullable = false)
    private Long operatorEntryId;
    
    @Column(name = "operator_exit_id")
    private Long operatorExitId;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "ticket_code", unique = true)
    private String ticketCode;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        // Usar UTC para evitar problemas de zona horaria
        LocalDateTime nowUtc = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        if (createdAt == null) {
            createdAt = nowUtc;
        }
        if (updatedAt == null) {
            updatedAt = nowUtc;
        }
        if (entryTime == null) {
            entryTime = nowUtc;
        }
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        // Usar UTC para evitar problemas de zona horaria
        updatedAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
