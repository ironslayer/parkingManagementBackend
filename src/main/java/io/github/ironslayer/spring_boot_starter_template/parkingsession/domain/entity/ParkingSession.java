package io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entidad de dominio para sesiones de parqueo.
 * Representa una sesión activa o histórica de un vehículo en el parqueadero.
 * 
 * Lógica de negocio incluida:
 * - Cálculo de tiempo estacionado
 * - Validación de estados de sesión
 * - Generación de códigos únicos
 */
public class ParkingSession {
    
    private Long id;
    private Long vehicleId;
    private Long parkingSpaceId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Long operatorEntryId;
    private Long operatorExitId;
    private Boolean isActive;
    private String ticketCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor por defecto
    public ParkingSession() {
        LocalDateTime now = LocalDateTime.now();
        this.entryTime = now;
        this.isActive = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Constructor para nueva sesión de entrada
    public ParkingSession(Long vehicleId, Long parkingSpaceId, Long operatorEntryId) {
        this();
        this.vehicleId = vehicleId;
        this.parkingSpaceId = parkingSpaceId;
        this.operatorEntryId = operatorEntryId;
    }

    // Constructor completo
    public ParkingSession(Long id, Long vehicleId, Long parkingSpaceId, 
                         LocalDateTime entryTime, LocalDateTime exitTime,
                         Long operatorEntryId, Long operatorExitId, 
                         Boolean isActive, String ticketCode,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.parkingSpaceId = parkingSpaceId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.operatorEntryId = operatorEntryId;
        this.operatorExitId = operatorExitId;
        this.isActive = isActive;
        this.ticketCode = ticketCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Registra la salida del vehículo
     * @param operatorExitId ID del operador que registra la salida
     */
    public void markExit(Long operatorExitId) {
        if (!this.isActive) {
            throw new IllegalStateException("Cannot mark exit for inactive session");
        }
        if (this.exitTime != null) {
            throw new IllegalStateException("Session already has exit time");
        }
        
        this.exitTime = LocalDateTime.now();
        this.operatorExitId = operatorExitId;
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calcula las horas estacionado desde la entrada hasta ahora (o hasta la salida si existe)
     * @return horas como decimal (ej: 1.5 para 1 hora y 30 minutos)
     */
    public double calculateParkedHours() {
        LocalDateTime endTime = (exitTime != null) ? exitTime : LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(entryTime, endTime);
        return Math.max(minutes / 60.0, 0.0);
    }

    /**
     * Genera un código único para el ticket basado en la sesión
     * Formato: T-YYYYMMDDHHMM-{sessionId}
     */
    public String generateTicketCode() {
        if (id == null) {
            throw new IllegalStateException("Cannot generate ticket code without session ID");
        }
        
        String timestamp = entryTime.toString()
            .replaceAll("[^0-9]", "")
            .substring(0, 12); // YYYYMMDDHHMM
            
        return String.format("T-%s-%03d", timestamp, id % 1000);
    }

    /**
     * Verifica si la sesión está activa y puede registrar salida
     */
    public boolean canRegisterExit() {
        return isActive && exitTime == null;
    }

    /**
     * Valida que los datos de la sesión son consistentes
     */
    public boolean isValid() {
        if (vehicleId == null || parkingSpaceId == null || operatorEntryId == null) {
            return false;
        }
        
        if (entryTime == null) {
            return false;
        }
        
        // Si hay tiempo de salida, debe ser después de entrada
        if (exitTime != null && exitTime.isBefore(entryTime)) {
            return false;
        }
        
        // Si no está activa, debe tener tiempo de salida
        if (!isActive && exitTime == null) {
            return false;
        }
        
        return true;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(Long parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Long getOperatorEntryId() {
        return operatorEntryId;
    }

    public void setOperatorEntryId(Long operatorEntryId) {
        this.operatorEntryId = operatorEntryId;
    }

    public Long getOperatorExitId() {
        return operatorExitId;
    }

    public void setOperatorExitId(Long operatorExitId) {
        this.operatorExitId = operatorExitId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
