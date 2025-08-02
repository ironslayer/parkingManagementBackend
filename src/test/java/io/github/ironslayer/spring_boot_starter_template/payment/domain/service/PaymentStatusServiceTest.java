package io.github.ironslayer.spring_boot_starter_template.payment.domain.service;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests para PaymentStatusService
 * Cubre la lógica de estados y timeouts de pagos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentStatusService Tests")
class PaymentStatusServiceTest {

    @InjectMocks
    private PaymentStatusService service;

    private Payment pendingPayment;
    private Payment paidPayment;
    private Payment cancelledPayment;

    @BeforeEach
    void setUp() {
        // Pago pendiente recién creado
        pendingPayment = Payment.createNewPayment(
            1L, new BigDecimal("10.00"), new BigDecimal("2.00"), 
            new BigDecimal("5.00"), PaymentMethod.CASH, 2L
        );
        pendingPayment.setId(1L);
        pendingPayment.setCreatedAt(LocalDateTime.now());

        // Pago completado
        paidPayment = Payment.createNewPayment(
            2L, new BigDecimal("15.00"), new BigDecimal("3.00"), 
            new BigDecimal("5.00"), PaymentMethod.CARD, 2L
        );
        paidPayment.setId(2L);
        paidPayment.markAsPaid();

        // Pago cancelado
        cancelledPayment = Payment.createNewPayment(
            3L, new BigDecimal("8.00"), new BigDecimal("1.60"), 
            new BigDecimal("5.00"), PaymentMethod.CASH, 2L
        );
        cancelledPayment.setId(3L);
        cancelledPayment.cancel();
    }

    @Test
    @DisplayName("Should not cancel payment due to timeout when recently created")
    void shouldNotCancelRecentPayment() {
        // Given
        pendingPayment.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        // When
        boolean shouldCancel = service.shouldCancelDueToTimeout(pendingPayment);

        // Then
        assertFalse(shouldCancel);
    }

    @Test
    @DisplayName("Should cancel payment due to timeout when exceeded 15 minutes")
    void shouldCancelPaymentDueToTimeout() {
        // Given
        pendingPayment.setCreatedAt(LocalDateTime.now().minusMinutes(20));

        // When
        boolean shouldCancel = service.shouldCancelDueToTimeout(pendingPayment);

        // Then
        assertTrue(shouldCancel);
    }

    @Test
    @DisplayName("Should not cancel paid payment regardless of time")
    void shouldNotCancelPaidPayment() {
        // Given
        paidPayment.setCreatedAt(LocalDateTime.now().minusMinutes(30));

        // When
        boolean shouldCancel = service.shouldCancelDueToTimeout(paidPayment);

        // Then
        assertFalse(shouldCancel);
    }

    @Test
    @DisplayName("Should not cancel already cancelled payment")
    void shouldNotCancelAlreadyCancelledPayment() {
        // Given
        cancelledPayment.setCreatedAt(LocalDateTime.now().minusMinutes(30));

        // When
        boolean shouldCancel = service.shouldCancelDueToTimeout(cancelledPayment);

        // Then
        assertFalse(shouldCancel);
    }

    @Test
    @DisplayName("Should return correct status descriptions")
    void shouldReturnCorrectStatusDescriptions() {
        // When & Then
        assertEquals("Payment is pending completion", 
                    service.getStatusDescription(PaymentStatus.PENDING));
        assertEquals("Payment completed successfully", 
                    service.getStatusDescription(PaymentStatus.PAID));
        assertEquals("Payment was cancelled or expired", 
                    service.getStatusDescription(PaymentStatus.CANCELLED));
    }

    @Test
    @DisplayName("Should return correct available actions for each status")
    void shouldReturnCorrectAvailableActions() {
        // When & Then
        String[] pendingActions = service.getAvailableActions(PaymentStatus.PENDING);
        assertArrayEquals(new String[]{"complete_payment", "cancel_payment"}, pendingActions);

        String[] paidActions = service.getAvailableActions(PaymentStatus.PAID);
        assertArrayEquals(new String[]{"refund_payment"}, paidActions);

        String[] cancelledActions = service.getAvailableActions(PaymentStatus.CANCELLED);
        assertArrayEquals(new String[]{}, cancelledActions);
    }

    @Test
    @DisplayName("Should validate status transitions correctly")
    void shouldValidateStatusTransitionsCorrectly() {
        // From PENDING
        assertTrue(service.isValidStatusTransition(PaymentStatus.PENDING, PaymentStatus.PAID));
        assertTrue(service.isValidStatusTransition(PaymentStatus.PENDING, PaymentStatus.CANCELLED));

        // From PAID (no transitions allowed)
        assertFalse(service.isValidStatusTransition(PaymentStatus.PAID, PaymentStatus.PENDING));
        assertFalse(service.isValidStatusTransition(PaymentStatus.PAID, PaymentStatus.CANCELLED));

        // From CANCELLED (no transitions allowed)
        assertFalse(service.isValidStatusTransition(PaymentStatus.CANCELLED, PaymentStatus.PENDING));
        assertFalse(service.isValidStatusTransition(PaymentStatus.CANCELLED, PaymentStatus.PAID));
    }

    @Test
    @DisplayName("Should calculate remaining time correctly")
    void shouldCalculateRemainingTimeCorrectly() {
        // Given
        pendingPayment.setCreatedAt(LocalDateTime.now().minusMinutes(10));

        // When
        long remainingMinutes = service.getRemainingTimeMinutes(pendingPayment);

        // Then
        assertEquals(5, remainingMinutes); // 15 - 10 = 5
    }

    @Test
    @DisplayName("Should return zero remaining time for non-pending payments")
    void shouldReturnZeroRemainingTimeForNonPendingPayments() {
        // When & Then
        assertEquals(0, service.getRemainingTimeMinutes(paidPayment));
        assertEquals(0, service.getRemainingTimeMinutes(cancelledPayment));
    }

    @Test
    @DisplayName("Should return zero remaining time when timeout exceeded")
    void shouldReturnZeroRemainingTimeWhenTimeoutExceeded() {
        // Given
        pendingPayment.setCreatedAt(LocalDateTime.now().minusMinutes(20));

        // When
        long remainingMinutes = service.getRemainingTimeMinutes(pendingPayment);

        // Then
        assertEquals(0, remainingMinutes);
    }

    @Test
    @DisplayName("Should detect near timeout correctly")
    void shouldDetectNearTimeoutCorrectly() {
        // Given - 12 minutos elapsed (quedan 3 minutos)
        pendingPayment.setCreatedAt(LocalDateTime.now().minusMinutes(12));

        // When
        boolean isNearTimeout = service.isNearTimeout(pendingPayment);

        // Then
        assertTrue(isNearTimeout);
    }

    @Test
    @DisplayName("Should not detect near timeout for early payments")
    void shouldNotDetectNearTimeoutForEarlyPayments() {
        // Given - 2 minutos elapsed (quedan 13 minutos)
        pendingPayment.setCreatedAt(LocalDateTime.now().minusMinutes(2));

        // When
        boolean isNearTimeout = service.isNearTimeout(pendingPayment);

        // Then
        assertFalse(isNearTimeout);
    }

    @Test
    @DisplayName("Should not detect near timeout for non-pending payments")
    void shouldNotDetectNearTimeoutForNonPendingPayments() {
        // When & Then
        assertFalse(service.isNearTimeout(paidPayment));
        assertFalse(service.isNearTimeout(cancelledPayment));
    }
}
