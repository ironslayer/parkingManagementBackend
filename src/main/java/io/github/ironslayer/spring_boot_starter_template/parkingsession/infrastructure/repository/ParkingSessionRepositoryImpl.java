package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.entity.ParkingSessionEntity;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.mapper.ParkingSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de sesiones de parqueo.
 * Adapta entre la capa de dominio y la capa de persistencia JPA.
 */
@Repository
@RequiredArgsConstructor
public class ParkingSessionRepositoryImpl implements ParkingSessionRepository {
    
    private final ParkingSessionJpaRepository jpaRepository;
    private final ParkingSessionMapper mapper;
    
    @Override
    public ParkingSession save(ParkingSession parkingSession) {
        ParkingSessionEntity entity = mapper.toEntity(parkingSession);
        ParkingSessionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<ParkingSession> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<ParkingSession> findActiveSessionByVehicleId(Long vehicleId) {
        return jpaRepository.findActiveSessionByVehicleId(vehicleId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<ParkingSession> findByTicketCode(String ticketCode) {
        return jpaRepository.findByTicketCode(ticketCode)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean hasActiveSession(Long vehicleId) {
        return jpaRepository.hasActiveSession(vehicleId);
    }
    
    @Override
    public List<ParkingSession> findAllActiveSessions() {
        return jpaRepository.findAllActiveSessions()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSession> findSessionsByVehicleId(Long vehicleId) {
        return jpaRepository.findSessionsByVehicleId(vehicleId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSession> findSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findSessionsByDateRange(startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<ParkingSession> findSessionsByOperatorId(Long operatorId) {
        return jpaRepository.findSessionsByOperatorId(operatorId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public long countActiveSessions() {
        return jpaRepository.countActiveSessions();
    }
    
    @Override
    public List<ParkingSession> findSessionsByParkingSpaceId(Long parkingSpaceId) {
        return jpaRepository.findSessionsByParkingSpaceId(parkingSpaceId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
