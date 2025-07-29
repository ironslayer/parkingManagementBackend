package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.repository;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.entity.PaymentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para pagos
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    
    /**
     * Buscar pago por ID de sesión de parqueo
     */
    Optional<PaymentJpaEntity> findByParkingSessionId(Long parkingSessionId);
    
    /**
     * Verificar si existe un pago para una sesión específica
     */
    boolean existsByParkingSessionId(Long parkingSessionId);
    
    /**
     * Buscar pagos por estado
     */
    List<PaymentJpaEntity> findByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Buscar pagos por operador
     */
    Page<PaymentJpaEntity> findByOperatorId(Long operatorId, Pageable pageable);
    
    /**
     * Buscar pagos creados en un rango de fechas
     */
    Page<PaymentJpaEntity> findByCreatedAtBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    /**
     * Buscar pagos pagados en un rango de fechas
     */
    Page<PaymentJpaEntity> findByPaidAtBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    /**
     * Contar pagos por estado
     */
    @Query("SELECT COUNT(p) FROM PaymentJpaEntity p WHERE p.paymentStatus = :status")
    long countByPaymentStatus(@Param("status") PaymentStatus status);
    
    /**
     * Buscar pagos pendientes más antiguos que una fecha específica
     */
    @Query("SELECT p FROM PaymentJpaEntity p WHERE p.paymentStatus = 'PENDING' AND p.createdAt < :cutoffDate")
    List<PaymentJpaEntity> findPendingPaymentsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
