package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.entity;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla de pagos
 */
@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "parking_session_id", nullable = false)
    private Long parkingSessionId;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "hours_parked", nullable = false, precision = 10, scale = 2)
    private BigDecimal hoursParked;
    
    @Column(name = "rate_applied", nullable = false, precision = 10, scale = 2)
    private BigDecimal rateApplied;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "operator_id", nullable = false)
    private Long operatorId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }
}
