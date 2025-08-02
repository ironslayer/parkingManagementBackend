package io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.ParkingSessionNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.PaymentCalculationException;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests para CalculateAmountHandler
 * Cubre la lógica de cálculo de montos de pago
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CalculateAmountHandler Tests")
class CalculateAmountHandlerTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private RateConfigRepository rateConfigRepository;

    @InjectMocks
    private CalculateAmountHandler handler;

    private CalculateAmountRequest validRequest;
    private ParkingSession completedSession;
    private Vehicle testVehicle;
    private RateConfig testRateConfig;

    @BeforeEach
    void setUp() {
        validRequest = new CalculateAmountRequest(1L);

        // Sesión de parqueo completada (2 horas)
        completedSession = new ParkingSession(
            1L, 1L, 1L, 
            LocalDateTime.now().minusHours(2), // entryTime
            LocalDateTime.now(), // exitTime
            2L, 2L, // operatorEntryId, operatorExitId
            false, // isActive
            "TICKET123",
            LocalDateTime.now().minusHours(2), // createdAt
            LocalDateTime.now() // updatedAt
        );

        // Vehículo de tipo 1 (auto)
        testVehicle = new Vehicle("ABC123", 1L, "Toyota", "Corolla", "White", "John Doe", "555-1234");
        testVehicle.setId(1L);

        // Configuración de tarifa: $5 por hora, mínimo 1 hora, máximo $50 diario
        testRateConfig = RateConfig.builder()
            .id(1L)
            .vehicleTypeId(1L)
            .ratePerHour(new BigDecimal("5.00"))
            .minimumChargeHours(1)
            .maximumDailyRate(new BigDecimal("50.00"))
            .isActive(true)
            .build();
    }

    @Test
    @DisplayName("Should throw BadRequestException when parking session ID is null")
    void shouldThrowExceptionWhenParkingSessionIdIsNull() {
        // Given
        CalculateAmountRequest invalidRequest = new CalculateAmountRequest(null);

        // When & Then
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> handler.handle(invalidRequest)
        );
        assertEquals("Bad Request Exception (400). Parking session ID is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ParkingSessionNotFoundException when session doesn't exist")
    void shouldThrowExceptionWhenSessionNotFound() {
        // Given
        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ParkingSessionNotFoundException exception = assertThrows(
            ParkingSessionNotFoundException.class,
            () -> handler.handle(validRequest)
        );
        assertEquals("Parking session with ID '1' not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw BadRequestException when session is still active")
    void shouldThrowExceptionWhenSessionIsActive() {
        // Given
        ParkingSession activeSession = new ParkingSession(1L, 1L, 2L);
        activeSession.setId(1L);

        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(activeSession));

        // When & Then
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> handler.handle(validRequest)
        );
        assertTrue(exception.getMessage().contains("Cannot calculate amount for an active parking session"));
    }

    @Test
    @DisplayName("Should throw VehicleNotFoundException when vehicle doesn't exist")
    void shouldThrowExceptionWhenVehicleNotFound() {
        // Given
        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        VehicleNotFoundException exception = assertThrows(
            VehicleNotFoundException.class,
            () -> handler.handle(validRequest)
        );
        assertEquals("Vehicle not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw PaymentCalculationException when no rate config found")
    void shouldThrowExceptionWhenNoRateConfigFound() {
        // Given
        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(rateConfigRepository.findActiveByVehicleTypeId(1L)).thenReturn(Optional.empty());

        // When & Then
        PaymentCalculationException exception = assertThrows(
            PaymentCalculationException.class,
            () -> handler.handle(validRequest)
        );
        assertTrue(exception.getMessage().contains("No active rate configuration found"));
    }

    @Test
    @DisplayName("Should calculate amount correctly for 2 hours parking")
    void shouldCalculateAmountCorrectlyForTwoHours() {
        // Given
        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(completedSession));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(rateConfigRepository.findActiveByVehicleTypeId(1L)).thenReturn(Optional.of(testRateConfig));

        // When
        CalculateAmountResponse response = handler.handle(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("10.00"), response.getTotalAmount()); // 2 horas * $5
        assertEquals(new BigDecimal("2.00"), response.getHoursParked());
        assertEquals(new BigDecimal("5.00"), response.getRatePerHour());
        assertEquals(Integer.valueOf(1), response.getMinimumChargeHours());
        assertEquals(new BigDecimal("50.00"), response.getMaximumDailyRate());
        assertEquals(Long.valueOf(1L), response.getParkingSessionId());
    }

    @Test
    @DisplayName("Should apply minimum charge hours for short parking")
    void shouldApplyMinimumChargeHoursForShortParking() {
        // Given - 30 minutos de parqueo
        ParkingSession shortSession = new ParkingSession(
            1L, 1L, 1L, 
            LocalDateTime.now().minusMinutes(30), // entryTime
            LocalDateTime.now(), // exitTime
            2L, 2L, // operatorEntryId, operatorExitId
            false, // isActive
            "TICKET123",
            LocalDateTime.now().minusMinutes(30), // createdAt
            LocalDateTime.now() // updatedAt
        );

        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(shortSession));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(rateConfigRepository.findActiveByVehicleTypeId(1L)).thenReturn(Optional.of(testRateConfig));

        // When
        CalculateAmountResponse response = handler.handle(validRequest);

        // Then
        // Debe cobrar 1 hora completa (mínimo) aunque solo estuvo 30 minutos
        assertEquals(new BigDecimal("5.00"), response.getTotalAmount());
        assertTrue(response.getHoursParked().compareTo(new BigDecimal("0.50")) == 0); // Tiempo real
    }

    @Test
    @DisplayName("Should apply maximum daily rate for long parking")
    void shouldApplyMaximumDailyRateForLongParking() {
        // Given - 12 horas de parqueo (excede el máximo diario)
        ParkingSession longSession = new ParkingSession(
            1L, 1L, 1L, 
            LocalDateTime.now().minusHours(12), // entryTime
            LocalDateTime.now(), // exitTime
            2L, 2L, // operatorEntryId, operatorExitId
            false, // isActive
            "TICKET123",
            LocalDateTime.now().minusHours(12), // createdAt
            LocalDateTime.now() // updatedAt
        );

        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(longSession));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(rateConfigRepository.findActiveByVehicleTypeId(1L)).thenReturn(Optional.of(testRateConfig));

        // When
        CalculateAmountResponse response = handler.handle(validRequest);

        // Then
        // Debe aplicar tarifa máxima diaria ($50) en lugar de $60 (12 horas * $5)
        assertEquals(new BigDecimal("50.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("12.00"), response.getHoursParked());
    }

    @Test
    @DisplayName("Should handle fractional hours correctly")
    void shouldHandleFractionalHoursCorrectly() {
        // Given - 1.5 horas (1 hora 30 minutos)
        ParkingSession fractionalSession = new ParkingSession(
            1L, 1L, 1L, 
            LocalDateTime.now().minusHours(1).minusMinutes(30), // entryTime
            LocalDateTime.now(), // exitTime
            2L, 2L, // operatorEntryId, operatorExitId
            false, // isActive
            "TICKET123",
            LocalDateTime.now().minusHours(1).minusMinutes(30), // createdAt
            LocalDateTime.now() // updatedAt
        );

        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(fractionalSession));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(rateConfigRepository.findActiveByVehicleTypeId(1L)).thenReturn(Optional.of(testRateConfig));

        // When
        CalculateAmountResponse response = handler.handle(validRequest);

        // Then
        // Debe redondear hacia arriba: 1.5 horas = 2 horas cobrables
        assertEquals(new BigDecimal("10.00"), response.getTotalAmount()); // 2 horas * $5
        assertEquals(new BigDecimal("1.50"), response.getHoursParked()); // Tiempo real registrado
    }

    @Test
    @DisplayName("Should handle zero parking time with minimum charge")
    void shouldHandleZeroParkingTimeWithMinimumCharge() {
        // Given - entrada y salida al mismo tiempo
        LocalDateTime now = LocalDateTime.now();
        ParkingSession zeroSession = new ParkingSession(
            1L, 1L, 1L, 
            now, // entryTime
            now, // exitTime
            2L, 2L, // operatorEntryId, operatorExitId
            false, // isActive
            "TICKET123",
            now, // createdAt
            now // updatedAt
        );

        when(parkingSessionRepository.findById(1L)).thenReturn(Optional.of(zeroSession));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(rateConfigRepository.findActiveByVehicleTypeId(1L)).thenReturn(Optional.of(testRateConfig));

        // When
        CalculateAmountResponse response = handler.handle(validRequest);

        // Then
        // Debe aplicar tarifa mínima (1 hora)
        assertEquals(new BigDecimal("5.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("0.00"), response.getHoursParked());
    }
}
