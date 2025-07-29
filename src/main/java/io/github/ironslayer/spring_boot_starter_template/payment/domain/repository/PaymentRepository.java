package io.github.ironslayer.spring_boot_starter_template.payment.domain.repository;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de pagos (Domain Layer)
 * Define las operaciones de persistencia necesarias para la entidad Payment
 */
public interface PaymentRepository {
    
    /**
     * Guardar un pago
     */
    Payment save(Payment payment);
    
    /**
     * Buscar pago por ID
     */
    Optional<Payment> findById(Long id);
    
    /**
     * Buscar pago por ID de sesión de parqueo
     */
    Optional<Payment> findByParkingSessionId(Long parkingSessionId);
    
    /**
     * Verificar si existe un pago para una sesión de parqueo específica
     */
    boolean existsByParkingSessionId(Long parkingSessionId);
    
    /**
     * Obtener todos los pagos
     */
    List<Payment> findAll();
    
    /**
     * Obtener pagos por operador
     */
    List<Payment> findByOperatorId(Long operatorId);
    
    /**
     * Obtener pagos por rango de fechas
     */
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Obtener pagos por estado
     */
    List<Payment> findByPaymentStatus(io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus status);
    
    /**
     * Obtener pagos realizados hoy
     */
    List<Payment> findPaymentsToday();
    
    /**
     * Eliminar pago por ID
     */
    void deleteById(Long id);
}
