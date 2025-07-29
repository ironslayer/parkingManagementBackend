package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.mapper;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.persistence.entity.PaymentJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio y entidades JPA de Payment
 */
@Component
public class PaymentMapper {
    
    /**
     * Convierte de entidad de dominio a entidad JPA
     */
    public PaymentJpaEntity toJpaEntity(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        return PaymentJpaEntity.builder()
                .id(payment.getId())
                .parkingSessionId(payment.getParkingSessionId())
                .totalAmount(payment.getTotalAmount())
                .hoursParked(payment.getHoursParked())
                .rateApplied(payment.getRateApplied())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paidAt(payment.getPaidAt())
                .operatorId(payment.getOperatorId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
    
    /**
     * Convierte de entidad JPA a entidad de dominio
     */
    public Payment toDomain(PaymentJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return Payment.builder()
                .id(jpaEntity.getId())
                .parkingSessionId(jpaEntity.getParkingSessionId())
                .totalAmount(jpaEntity.getTotalAmount())
                .hoursParked(jpaEntity.getHoursParked())
                .rateApplied(jpaEntity.getRateApplied())
                .paymentMethod(jpaEntity.getPaymentMethod())
                .paymentStatus(jpaEntity.getPaymentStatus())
                .paidAt(jpaEntity.getPaidAt())
                .operatorId(jpaEntity.getOperatorId())
                .createdAt(jpaEntity.getCreatedAt())
                .build();
    }
}
