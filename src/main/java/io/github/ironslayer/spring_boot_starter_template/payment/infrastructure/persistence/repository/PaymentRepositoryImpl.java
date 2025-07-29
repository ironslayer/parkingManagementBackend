package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.repository;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.repository.PaymentRepository;
import io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.entity.PaymentJpaEntity;
import io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de pagos usando JPA
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Payment save(Payment payment) {
        log.debug("Saving payment: {}", payment);
        
        PaymentJpaEntity jpaEntity = paymentMapper.toJpaEntity(payment);
        PaymentJpaEntity savedEntity = paymentJpaRepository.save(jpaEntity);
        Payment savedPayment = paymentMapper.toDomain(savedEntity);
        
        log.debug("Payment saved successfully with ID: {}", savedPayment.getId());
        return savedPayment;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        log.debug("Finding payment by ID: {}", id);
        
        return paymentJpaRepository.findById(id)
                .map(paymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByParkingSessionId(Long parkingSessionId) {
        log.debug("Finding payment by parking session ID: {}", parkingSessionId);
        
        return paymentJpaRepository.findByParkingSessionId(parkingSessionId)
                .map(paymentMapper::toDomain);
    }

    @Override
    public boolean existsByParkingSessionId(Long parkingSessionId) {
        log.debug("Checking if payment exists for parking session ID: {}", parkingSessionId);
        
        return paymentJpaRepository.existsByParkingSessionId(parkingSessionId);
    }

    @Override
    public List<Payment> findAll() {
        log.debug("Finding all payments");
        
        return paymentJpaRepository.findAll()
                .stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByOperatorId(Long operatorId) {
        log.debug("Finding payments by operator ID: {}", operatorId);
        
        return paymentJpaRepository.findByOperatorId(operatorId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding payments created between {} and {}", startDate, endDate);
        
        return paymentJpaRepository.findByCreatedAtBetween(startDate, endDate, Pageable.unpaged())
                .getContent()
                .stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByPaymentStatus(PaymentStatus status) {
        log.debug("Finding payments by status: {}", status);
        
        return paymentJpaRepository.findByPaymentStatus(status)
                .stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findPaymentsToday() {
        log.debug("Finding payments created today");
        
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        
        return findByCreatedAtBetween(startOfDay, endOfDay);
    }

    // Additional pageable methods for more complex queries
    public Page<Payment> findByOperator(Long operatorId, Pageable pageable) {
        log.debug("Finding payments by operator ID: {} with pageable: {}", operatorId, pageable);
        
        return paymentJpaRepository.findByOperatorId(operatorId, pageable)
                .map(paymentMapper::toDomain);
    }

    public Page<Payment> findByCreatedAtBetweenPageable(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Finding payments created between {} and {} with pageable: {}", startDate, endDate, pageable);
        
        return paymentJpaRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(paymentMapper::toDomain);
    }

    public Page<Payment> findByPaidAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Finding payments paid between {} and {} with pageable: {}", startDate, endDate, pageable);
        
        return paymentJpaRepository.findByPaidAtBetween(startDate, endDate, pageable)
                .map(paymentMapper::toDomain);
    }

    public long countByStatus(PaymentStatus status) {
        log.debug("Counting payments by status: {}", status);
        
        return paymentJpaRepository.countByPaymentStatus(status);
    }

    public List<Payment> findPendingPaymentsOlderThan(LocalDateTime cutoffDate) {
        log.debug("Finding pending payments older than: {}", cutoffDate);
        
        return paymentJpaRepository.findPendingPaymentsOlderThan(cutoffDate)
                .stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Deleting payment by ID: {}", id);
        paymentJpaRepository.deleteById(id);
        log.debug("Payment deleted successfully");
    }
}
