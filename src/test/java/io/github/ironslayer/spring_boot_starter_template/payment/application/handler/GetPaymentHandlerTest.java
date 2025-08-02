package io.github.ironslayer.spring_boot_starter_template.payment.application.handler;

import io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment.GetPaymentRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment.GetPaymentResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment.GetPaymentHandler;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.repository.PaymentRepository;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.PaymentNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests para GetPaymentHandler
 * Cubre la obtención de pagos por ID con validaciones
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GetPaymentHandler Tests")
class GetPaymentHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private GetPaymentHandler handler;

    private Payment existingPayment;
    private GetPaymentRequest validRequest;

    @BeforeEach
    void setUp() {
        // Pago existente
        existingPayment = Payment.createNewPayment(
            1L, new BigDecimal("15.00"), new BigDecimal("3.00"), 
            new BigDecimal("5.00"), PaymentMethod.CARD, 2L
        );
        existingPayment.setId(1L);
        existingPayment.markAsPaid();

        // Request válido
        validRequest = GetPaymentRequest.byId(1L);
    }

    @Nested
    @DisplayName("Request Validation")
    class RequestValidationTests {

        @Test
        @DisplayName("Should throw exception when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThrows(
                NullPointerException.class,
                () -> handler.handle(null)
            );
            verify(paymentRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw exception when both IDs are null")
        void shouldThrowExceptionWhenPaymentIdIsNull() {
            // Given
            GetPaymentRequest invalidRequest = GetPaymentRequest.byId(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.handle(invalidRequest)
            );
            assertEquals("Either paymentId or parkingSessionId must be provided", exception.getMessage());
            verify(paymentRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw PaymentNotFoundException when payment ID is zero")
        void shouldThrowExceptionWhenPaymentIdIsZero() {
            // Given
            GetPaymentRequest invalidRequest = GetPaymentRequest.byId(0L);
            when(paymentRepository.findById(0L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(
                PaymentNotFoundException.class,
                () -> handler.handle(invalidRequest)
            );
            verify(paymentRepository).findById(0L);
        }

        @Test
        @DisplayName("Should throw PaymentNotFoundException when payment ID is negative")
        void shouldThrowExceptionWhenPaymentIdIsNegative() {
            // Given
            GetPaymentRequest invalidRequest = GetPaymentRequest.byId(-1L);
            when(paymentRepository.findById(-1L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(
                PaymentNotFoundException.class,
                () -> handler.handle(invalidRequest)
            );
            verify(paymentRepository).findById(-1L);
        }
    }

    @Nested
    @DisplayName("Payment Retrieval")
    class PaymentRetrievalTests {

        @Test
        @DisplayName("Should throw PaymentNotFoundException when payment does not exist")
        void shouldThrowPaymentNotFoundExceptionWhenPaymentDoesNotExist() {
            // Given
            GetPaymentRequest request = GetPaymentRequest.byId(999L);
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> handler.handle(request)
            );
            assertEquals("Payment not found with ID: 999", exception.getMessage());
            verify(paymentRepository).findById(999L);
        }

        @Test
        @DisplayName("Should successfully retrieve payment when it exists")
        void shouldSuccessfullyRetrievePaymentWhenItExists() {
            // Given
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(existingPayment));

            // When
            GetPaymentResponse response = handler.handle(validRequest);

            // Then
            assertNotNull(response);
            assertEquals(existingPayment.getId(), response.getId());
            assertEquals(existingPayment.getParkingSessionId(), response.getParkingSessionId());
            assertEquals(existingPayment.getTotalAmount(), response.getAmount());
            assertEquals(existingPayment.getPaymentMethod().name(), response.getPaymentMethod());
            assertEquals(existingPayment.getPaymentStatus().name(), response.getStatus());
            assertEquals(existingPayment.getCreatedAt(), response.getCreatedAt());
            assertEquals(existingPayment.getPaidAt(), response.getPaidAt());

            verify(paymentRepository).findById(1L);
        }
    }

    @Nested
    @DisplayName("Payment Status Scenarios")
    class PaymentStatusScenariosTests {

        @Test
        @DisplayName("Should retrieve pending payment correctly")
        void shouldRetrievePendingPaymentCorrectly() {
            // Given
            Payment pendingPayment = Payment.createNewPayment(
                2L, new BigDecimal("10.00"), new BigDecimal("2.00"), 
                new BigDecimal("5.00"), PaymentMethod.CASH, 1L
            );
            pendingPayment.setId(2L);
            // No llamamos markAsPaid(), entonces queda PENDING

            GetPaymentRequest request = GetPaymentRequest.byId(2L);
            when(paymentRepository.findById(2L)).thenReturn(Optional.of(pendingPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(PaymentStatus.PENDING.name(), response.getStatus());
            assertNull(response.getPaidAt());
            verify(paymentRepository).findById(2L);
        }

        @Test
        @DisplayName("Should retrieve paid payment correctly")
        void shouldRetrievePaidPaymentCorrectly() {
            // Given
            Payment paidPayment = Payment.createNewPayment(
                3L, new BigDecimal("20.00"), new BigDecimal("4.00"), 
                new BigDecimal("5.00"), PaymentMethod.CARD, 1L
            );
            paidPayment.setId(3L);
            paidPayment.markAsPaid();

            GetPaymentRequest request = GetPaymentRequest.byId(3L);
            when(paymentRepository.findById(3L)).thenReturn(Optional.of(paidPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(PaymentStatus.PAID.name(), response.getStatus());
            assertNotNull(response.getPaidAt());
            verify(paymentRepository).findById(3L);
        }

        @Test
        @DisplayName("Should retrieve cancelled payment correctly")
        void shouldRetrieveCancelledPaymentCorrectly() {
            // Given
            Payment cancelledPayment = Payment.createNewPayment(
                4L, new BigDecimal("25.00"), new BigDecimal("5.00"), 
                new BigDecimal("5.00"), PaymentMethod.CASH, 1L
            );
            cancelledPayment.setId(4L);
            cancelledPayment.cancel();

            GetPaymentRequest request = GetPaymentRequest.byId(4L);
            when(paymentRepository.findById(4L)).thenReturn(Optional.of(cancelledPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(PaymentStatus.CANCELLED.name(), response.getStatus());
            assertNull(response.getPaidAt());
            verify(paymentRepository).findById(4L);
        }
    }

    @Nested
    @DisplayName("Payment Method Scenarios")
    class PaymentMethodScenariosTests {

        @Test
        @DisplayName("Should retrieve cash payment correctly")
        void shouldRetrieveCashPaymentCorrectly() {
            // Given
            Payment cashPayment = Payment.createNewPayment(
                5L, new BigDecimal("12.50"), new BigDecimal("2.5"), 
                new BigDecimal("5.00"), PaymentMethod.CASH, 1L
            );
            cashPayment.setId(5L);
            cashPayment.markAsPaid();

            GetPaymentRequest request = GetPaymentRequest.byId(5L);
            when(paymentRepository.findById(5L)).thenReturn(Optional.of(cashPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(PaymentMethod.CASH.name(), response.getPaymentMethod());
            verify(paymentRepository).findById(5L);
        }

        @Test
        @DisplayName("Should retrieve card payment correctly")
        void shouldRetrieveCardPaymentCorrectly() {
            // Given
            Payment cardPayment = Payment.createNewPayment(
                6L, new BigDecimal("30.00"), new BigDecimal("6.0"), 
                new BigDecimal("5.00"), PaymentMethod.CARD, 1L
            );
            cardPayment.setId(6L);
            cardPayment.markAsPaid();

            GetPaymentRequest request = GetPaymentRequest.byId(6L);
            when(paymentRepository.findById(6L)).thenReturn(Optional.of(cardPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(PaymentMethod.CARD.name(), response.getPaymentMethod());
            verify(paymentRepository).findById(6L);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle payment with zero amount")
        void shouldHandlePaymentWithZeroAmount() {
            // Given
            Payment zeroAmountPayment = Payment.createNewPayment(
                7L, BigDecimal.ZERO, BigDecimal.ZERO, 
                new BigDecimal("5.00"), PaymentMethod.CASH, 1L
            );
            zeroAmountPayment.setId(7L);

            GetPaymentRequest request = GetPaymentRequest.byId(7L);
            when(paymentRepository.findById(7L)).thenReturn(Optional.of(zeroAmountPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(BigDecimal.ZERO, response.getAmount());
            verify(paymentRepository).findById(7L);
        }

        @Test
        @DisplayName("Should handle payment with high amount")
        void shouldHandlePaymentWithHighAmount() {
            // Given
            Payment highAmountPayment = Payment.createNewPayment(
                8L, new BigDecimal("999.99"), new BigDecimal("199.99"), 
                new BigDecimal("5.00"), PaymentMethod.CARD, 1L
            );
            highAmountPayment.setId(8L);

            GetPaymentRequest request = GetPaymentRequest.byId(8L);
            when(paymentRepository.findById(8L)).thenReturn(Optional.of(highAmountPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(new BigDecimal("999.99"), response.getAmount());
            verify(paymentRepository).findById(8L);
        }

        @Test
        @DisplayName("Should handle payment with maximum Long ID")
        void shouldHandlePaymentWithMaximumLongId() {
            // Given
            Long maxId = Long.MAX_VALUE;
            Payment maxIdPayment = Payment.createNewPayment(
                1L, new BigDecimal("10.00"), new BigDecimal("2.0"), 
                new BigDecimal("5.00"), PaymentMethod.CASH, 1L
            );
            maxIdPayment.setId(maxId);

            GetPaymentRequest request = GetPaymentRequest.byId(maxId);
            when(paymentRepository.findById(maxId)).thenReturn(Optional.of(maxIdPayment));

            // When
            GetPaymentResponse response = handler.handle(request);

            // Then
            assertNotNull(response);
            assertEquals(maxId, response.getId());
            verify(paymentRepository).findById(maxId);
        }
    }
}
