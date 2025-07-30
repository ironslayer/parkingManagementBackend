package io.github.ironslayer.spring_boot_starter_template.payment.domain.enums;

/**
 * Enum que representa los estados de un pago en el sistema
 */
public enum PaymentStatus {
    PENDING,   // Pendiente de pago
    PAID,      // Pago exitoso
    CANCELLED  // Cancelado
}
