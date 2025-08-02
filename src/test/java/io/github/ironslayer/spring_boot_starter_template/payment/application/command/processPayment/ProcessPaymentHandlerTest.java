package io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.ParkingSessionNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.payment.application.command.calculateAmount.CalculateAmountHandler;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.PaymentAlreadyExistsException;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.repository.PaymentRepository;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;

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
 * Unit tests para ProcessPaymentHandler
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ProcessPaymentHandler Tests")
class ProcessPaymentHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private ParkingSessionRepository parkingSessionRepository;
    
    @Mock
    private CalculateAmountHandler calculateAmountHandler;
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProcessPaymentHandler processPaymentHandler;

    private ParkingSession completedSession;
    private Payment payment;
    private ProcessPaymentRequest request;

    @BeforeEach
    void setUp() {
        // Crear VehicleType usando builder
        VehicleType vehicleType = VehicleType.builder()
                .id(1L)
                .name("Car")
                .description("Standard car")
                .build();

        // Crear Vehicle usando setters
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setLicensePlate("ABC123");
        vehicle.setVehicleType(vehicleType);

        // Crear RateConfig usando builder
        RateConfig rateConfig = RateConfig.builder()
                .id(1L)
                .hourlyRate(BigDecimal.valueOf(10.00))
                .vehicleType(vehicleType)
                .build();

        // Crear ParkingSession completada (isActive = false, exitTime establecido)
        completedSession = new ParkingSession();
        completedSession.setId(1L);
        completedSession.setVehicle(vehicle);
        completedSession.setStartTime(LocalDateTime.now().minusHours(2));
        completedSession.setExitTime(LocalDateTime.now());
        completedSession.setIsActive(false); // Sesión completada
        completedSession.setTotalAmount(BigDecimal.valueOf(20.00));

        // Crear Payment
        payment = Payment.builder()
                .id(1L)
                .parkingSessionId(1L)
                .totalAmount(BigDecimal.valueOf(20.00))
                .hoursParked(BigDecimal.valueOf(2.0))
                .rateApplied(BigDecimal.valueOf(10.00))
                .paymentMethod(PaymentMethod.CASH)
                .paymentStatus(PaymentStatus.PAID)
                .operatorId(1L)
                .createdAt(LocalDateTime.now())
                .paidAt(LocalDateTime.now())
                .build();

        // Crear request
        request = ProcessPaymentRequest.builder()
                .sessionId(1L)
                .paymentMethod(PaymentMethod.CASH)
                .build();
    }

    @Nested
    @DisplayName("Process Payment Success Scenarios")
    class ProcessPaymentSuccessScenarios {

        @Test
        @DisplayName("Should process payment successfully for completed session")
        void shouldProcessPaymentSuccessfully() {
            // Given
            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
            when(paymentRepository.findByParkingSession(completedSession)).thenReturn(Optional.empty());
            when(calculateAmountHandler.handle(any())).thenReturn(BigDecimal.valueOf(20.00));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            // When
            Payment result = processPaymentHandler.handle(request);

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(20.00), result.getAmount());
            assertEquals(PaymentMethod.CASH, result.getPaymentMethod());
            assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
            
            verify(parkingSessionRepository).findById(1L);
            verify(paymentRepository).findByParkingSession(completedSession);
            verify(calculateAmountHandler).handle(any());
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("Should process payment with CREDIT_CARD method")
        void shouldProcessPaymentWithCreditCard() {
            // Given
            ProcessPaymentRequest creditCardRequest = ProcessPaymentRequest.builder()
                    .sessionId(1L)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
            when(paymentRepository.findByParkingSession(completedSession)).thenReturn(Optional.empty());
            when(calculateAmountHandler.handle(any())).thenReturn(BigDecimal.valueOf(20.00));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            // When
            Payment result = processPaymentHandler.handle(creditCardRequest);

            // Then
            assertNotNull(result);
            assertEquals(PaymentMethod.CREDIT_CARD, creditCardRequest.getPaymentMethod());
            
            verify(paymentRepository).save(argThat(p -> 
                p.getPaymentMethod() == PaymentMethod.CREDIT_CARD
            ));
        }
    }

    @Nested
    @DisplayName("Process Payment Error Scenarios")
    class ProcessPaymentErrorScenarios {

        @Test
        @DisplayName("Should throw exception when session not found")
        void shouldThrowExceptionWhenSessionNotFound() {
            // Given
            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ParkingSessionNotFoundException.class, 
                () -> processPaymentHandler.handle(request));
            
            verify(parkingSessionRepository).findById(1L);
            verifyNoInteractions(paymentRepository);
        }

        @Test
        @DisplayName("Should throw exception when session is still active")
        void shouldThrowExceptionWhenSessionIsActive() {
            // Given
            ParkingSession activeSession = new ParkingSession();
            activeSession.setId(1L);
            activeSession.setIsActive(true); // Sesión activa
            
            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(activeSession));

            // When & Then
            assertThrows(BadRequestException.class, 
                () -> processPaymentHandler.handle(request));
            
            verify(parkingSessionRepository).findById(1L);
            verifyNoInteractions(paymentRepository);
        }

        @Test
        @DisplayName("Should throw exception when payment already exists")
        void shouldThrowExceptionWhenPaymentAlreadyExists() {
            // Given
            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
            when(paymentRepository.findByParkingSession(completedSession)).thenReturn(Optional.of(payment));

            // When & Then
            assertThrows(PaymentAlreadyExistsException.class, 
                () -> processPaymentHandler.handle(request));
            
            verify(parkingSessionRepository).findById(1L);
            verify(paymentRepository).findByParkingSession(completedSession);
            verifyNoMoreInteractions(paymentRepository);
        }

        @Test
        @DisplayName("Should throw exception when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThrows(BadRequestException.class, 
                () -> processPaymentHandler.handle(null));
            
            verifyNoInteractions(parkingSessionRepository);
            verifyNoInteractions(paymentRepository);
        }

        @Test
        @DisplayName("Should throw exception when session ID is null")
        void shouldThrowExceptionWhenSessionIdIsNull() {
            // Given
            ProcessPaymentRequest invalidRequest = ProcessPaymentRequest.builder()
                    .sessionId(null)
                    .paymentMethod(PaymentMethod.CASH)
                    .build();

            // When & Then
            assertThrows(BadRequestException.class, 
                () -> processPaymentHandler.handle(invalidRequest));
        }

        @Test
        @DisplayName("Should throw exception when payment method is null")
        void shouldThrowExceptionWhenPaymentMethodIsNull() {
            // Given
            ProcessPaymentRequest invalidRequest = ProcessPaymentRequest.builder()
                    .sessionId(1L)
                    .paymentMethod(null)
                    .build();

            // When & Then
            assertThrows(BadRequestException.class, 
                () -> processPaymentHandler.handle(invalidRequest));
        }
    }

    @Nested
    @DisplayName("Payment Amount Calculation")
    class PaymentAmountCalculation {

        @Test
        @DisplayName("Should use calculated amount from CalculateAmountHandler")
        void shouldUseCalculatedAmount() {
            // Given
            BigDecimal expectedAmount = BigDecimal.valueOf(35.50);
            
            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
            when(paymentRepository.findByParkingSession(completedSession)).thenReturn(Optional.empty());
            when(calculateAmountHandler.handle(any())).thenReturn(expectedAmount);
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            // When
            processPaymentHandler.handle(request);

            // Then
            verify(calculateAmountHandler).handle(argThat(req -> 
                req.getSessionId().equals(1L)
            ));
            
            verify(paymentRepository).save(argThat(p -> 
                p.getAmount().equals(expectedAmount)
            ));
        }

        @Test
        @DisplayName("Should handle zero amount calculation")
        void shouldHandleZeroAmount() {
            // Given
            BigDecimal zeroAmount = BigDecimal.ZERO;
            
            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
            when(paymentRepository.findByParkingSession(completedSession)).thenReturn(Optional.empty());
            when(calculateAmountHandler.handle(any())).thenReturn(zeroAmount);
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            // When
            Payment result = processPaymentHandler.handle(request);

            // Then
            verify(paymentRepository).save(argThat(p -> 
                p.getAmount().equals(BigDecimal.ZERO)
            ));
        }
    }

    @Nested
    @DisplayName("Payment Status and Method Validation")
    class PaymentStatusAndMethodValidation {

        @Test
        @DisplayName("Should set payment status to COMPLETED for CASH payment")
        void shouldSetStatusCompletedForCash() {
            // Given
            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
            when(paymentRepository.findByParkingSession(completedSession)).thenReturn(Optional.empty());
            when(calculateAmountHandler.handle(any())).thenReturn(BigDecimal.valueOf(20.00));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            // When
            processPaymentHandler.handle(request);

            // Then
            verify(paymentRepository).save(argThat(p -> 
                p.getPaymentStatus() == PaymentStatus.COMPLETED
            ));
        }

        @Test
        @DisplayName("Should set payment status to COMPLETED for CREDIT_CARD payment")
        void shouldSetStatusCompletedForCreditCard() {
            // Given
            ProcessPaymentRequest creditCardRequest = ProcessPaymentRequest.builder()
                    .sessionId(1L)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
            when(paymentRepository.findByParkingSession(completedSession)).thenReturn(Optional.empty());
            when(calculateAmountHandler.handle(any())).thenReturn(BigDecimal.valueOf(20.00));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            // When
            processPaymentHandler.handle(creditCardRequest);

            // Then
            verify(paymentRepository).save(argThat(p -> 
                p.getPaymentStatus() == PaymentStatus.COMPLETED &&
                p.getPaymentMethod() == PaymentMethod.CREDIT_CARD
            ));
        }
    }
}
