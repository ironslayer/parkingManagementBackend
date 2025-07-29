package io.github.ironslayer.spring_boot_starter_template.payment.domain.enums;

/**
 * Enum que representa los métodos de pago disponibles en el sistema
 */
public enum PaymentMethod {
    CASH,      // Efectivo
    CARD,      // Tarjeta (débito/crédito)
    TRANSFER   // Transferencia bancaria
}
