package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoActiveSessionException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.ParkingSessionNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.entity.VehicleType;
import io.github.ironslayer.spring_boot_starter_template.vehicletype.domain.port.VehicleTypeRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EndSessionHandler Tests")
class EndSessionHandlerTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private ParkingSpaceRepository parkingSpaceRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VehicleTypeRepository vehicleTypeRepository;
    
    @Mock
    private RateConfigRepository rateConfigRepository;

    @InjectMocks
    private EndSessionHandler endSessionHandler;

    @Nested
    @DisplayName("Request Validation Tests")
    class RequestValidationTests {

        @Test
        @DisplayName("Should throw BadRequestException when operator ID is null")
        void shouldThrowBadRequestExceptionWhenOperatorIdIsNull() {
            // Given
            EndSessionRequest request = new EndSessionRequest("ABC123", null, null, null);

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). Operator ID is required");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                parkingSpaceRepository, userRepository, vehicleTypeRepository, rateConfigRepository);
        }

        @Test
        @DisplayName("Should throw BadRequestException when no identification parameter provided")
        void shouldThrowBadRequestExceptionWhenNoIdentificationParameterProvided() {
            // Given
            EndSessionRequest request = new EndSessionRequest(null, null, null, 1L);

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). Provide exactly one of: license plate, session ID, or ticket code");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                parkingSpaceRepository, userRepository, vehicleTypeRepository, rateConfigRepository);
        }

        @Test
        @DisplayName("Should throw BadRequestException when multiple identification parameters provided")
        void shouldThrowBadRequestExceptionWhenMultipleIdentificationParametersProvided() {
            // Given
            EndSessionRequest request = new EndSessionRequest("ABC123", 1L, "T-123", 1L);

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). Provide exactly one of: license plate, session ID, or ticket code");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                parkingSpaceRepository, userRepository, vehicleTypeRepository, rateConfigRepository);
        }
    }

    @Nested
    @DisplayName("Session Identification by License Plate Tests")
    class SessionIdentificationByLicensePlateTests {

        @Test
        @DisplayName("Should throw VehicleNotFoundException when vehicle does not exist")
        void shouldThrowVehicleNotFoundExceptionWhenVehicleDoesNotExist() {
            // Given
            EndSessionRequest request = new EndSessionRequest("INVALID123", null, null, 1L);
            
            when(vehicleRepository.findByLicensePlate("INVALID123"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(VehicleNotFoundException.class);

            verify(vehicleRepository).findByLicensePlate("INVALID123");
            verifyNoInteractions(parkingSessionRepository, parkingSpaceRepository, 
                                userRepository, vehicleTypeRepository, rateConfigRepository);
        }

        @Test
        @DisplayName("Should throw NoActiveSessionException when vehicle has no active session")
        void shouldThrowNoActiveSessionExceptionWhenVehicleHasNoActiveSession() {
            // Given
            EndSessionRequest request = new EndSessionRequest("ABC123", null, null, 1L);
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.findActiveSessionByVehicleId(1L))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(NoActiveSessionException.class);

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).findActiveSessionByVehicleId(1L);
            verifyNoInteractions(parkingSpaceRepository, userRepository, vehicleTypeRepository, rateConfigRepository);
        }
    }

    @Nested
    @DisplayName("Session Identification by Session ID Tests")
    class SessionIdentificationBySessionIdTests {

        @Test
        @DisplayName("Should throw ParkingSessionNotFoundException when session does not exist")
        void shouldThrowParkingSessionNotFoundExceptionWhenSessionDoesNotExist() {
            // Given
            EndSessionRequest request = new EndSessionRequest(null, 999L, null, 1L);
            
            when(parkingSessionRepository.findById(999L))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(ParkingSessionNotFoundException.class);

            verify(parkingSessionRepository).findById(999L);
            verifyNoInteractions(vehicleRepository, parkingSpaceRepository, 
                                userRepository, vehicleTypeRepository, rateConfigRepository);
        }

        @Test
        @DisplayName("Should throw NoActiveSessionException when session cannot register exit")
        void shouldThrowNoActiveSessionExceptionWhenSessionCannotRegisterExit() {
            // Given
            EndSessionRequest request = new EndSessionRequest(null, 1L, null, 1L);
            ParkingSession inactiveSession = createMockParkingSession(1L, false);
            
            when(parkingSessionRepository.findById(1L))
                    .thenReturn(Optional.of(inactiveSession));
            when(inactiveSession.canRegisterExit())
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(NoActiveSessionException.class);

            verify(parkingSessionRepository).findById(1L);
            verify(inactiveSession).canRegisterExit();
            verifyNoInteractions(vehicleRepository, parkingSpaceRepository, 
                                userRepository, vehicleTypeRepository, rateConfigRepository);
        }
    }

    @Nested
    @DisplayName("Session Identification by Ticket Code Tests")
    class SessionIdentificationByTicketCodeTests {

        @Test
        @DisplayName("Should throw ParkingSessionNotFoundException when ticket code not found")
        void shouldThrowParkingSessionNotFoundExceptionWhenTicketCodeNotFound() {
            // Given
            EndSessionRequest request = new EndSessionRequest(null, null, "T-INVALID", 1L);
            
            when(parkingSessionRepository.findByTicketCode("T-INVALID"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(ParkingSessionNotFoundException.class);

            verify(parkingSessionRepository).findByTicketCode("T-INVALID");
            verifyNoInteractions(vehicleRepository, parkingSpaceRepository, 
                                userRepository, vehicleTypeRepository, rateConfigRepository);
        }
    }

    @Nested
    @DisplayName("Operator Validation Tests")
    class OperatorValidationTests {

        @Test
        @DisplayName("Should throw UserNotFoundException when operator does not exist")
        void shouldThrowUserNotFoundExceptionWhenOperatorDoesNotExist() {
            // Given
            EndSessionRequest request = new EndSessionRequest("ABC123", null, null, 999L);
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            ParkingSession activeSession = createMockParkingSession(1L, true);
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.findActiveSessionByVehicleId(1L))
                    .thenReturn(Optional.of(activeSession));
            when(userRepository.findById(999L))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> endSessionHandler.handle(request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User with ID '999' not found");

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).findActiveSessionByVehicleId(1L);
            verify(userRepository).findById(999L);
            verifyNoInteractions(parkingSpaceRepository, vehicleTypeRepository, rateConfigRepository);
        }
    }

    @Nested
    @DisplayName("Successful Session End Tests")
    class SuccessfulSessionEndTests {

        @Test
        @DisplayName("Should end parking session successfully with valid data")
        void shouldEndParkingSessionSuccessfullyWithValidData() {
            // Given
            EndSessionRequest request = new EndSessionRequest("ABC123", null, null, 1L);
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            ParkingSession activeSession = createMockParkingSession(1L, true);
            User operator = createMockUser(1L, "John", "Doe");
            ParkingSpace parkingSpace = createMockParkingSpace(1L, "A01");
            VehicleType vehicleType = createMockVehicleType(1L, "CAR");
            RateConfig rateConfig = createMockRateConfig();
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.findActiveSessionByVehicleId(1L))
                    .thenReturn(Optional.of(activeSession));
            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(operator));
            when(activeSession.getVehicleId()).thenReturn(1L);
            when(activeSession.getParkingSpaceId()).thenReturn(1L);
            when(activeSession.calculateParkedHours()).thenReturn(2.5);
            when(vehicleRepository.findById(1L))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSpaceRepository.findById(1L))
                    .thenReturn(Optional.of(parkingSpace));
            when(vehicleTypeRepository.findById(1L))
                    .thenReturn(Optional.of(vehicleType));
            when(rateConfigRepository.findActiveByVehicleTypeId(1L))
                    .thenReturn(Optional.of(rateConfig));
            when(parkingSessionRepository.save(any(ParkingSession.class)))
                    .thenReturn(activeSession);
            when(parkingSpaceRepository.save(any(ParkingSpace.class)))
                    .thenReturn(parkingSpace);
            when(activeSession.getId()).thenReturn(1L);
            when(activeSession.getEntryTime()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
            when(activeSession.getExitTime()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 30));

            // When
            EndSessionResponse response = endSessionHandler.handle(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.sessionId()).isEqualTo(1L);
            assertThat(response.licensePlate()).isEqualTo("ABC123");
            assertThat(response.vehicleType()).isEqualTo("CAR");
            assertThat(response.parkingSpace()).isEqualTo("A01");
            assertThat(response.operatorName()).isEqualTo("John Doe");
            assertThat(response.entryTime()).isNotBlank();
            assertThat(response.exitTime()).isNotBlank();

            // Verify interactions
            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).findActiveSessionByVehicleId(1L);
            verify(userRepository).findById(1L);
            verify(activeSession).markExit(1L);
            verify(parkingSpace).free();
            verify(parkingSessionRepository).save(activeSession);
            verify(parkingSpaceRepository).save(parkingSpace);
        }
    }

    // Helper methods for creating test objects
    private Vehicle createMockVehicle(Long id, String licensePlate, Long vehicleTypeId) {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getId()).thenReturn(id);
        when(vehicle.getLicensePlate()).thenReturn(licensePlate);
        when(vehicle.getVehicleTypeId()).thenReturn(vehicleTypeId);
        return vehicle;
    }

    private ParkingSession createMockParkingSession(Long id, boolean canRegisterExit) {
        ParkingSession session = mock(ParkingSession.class);
        when(session.getId()).thenReturn(id);
        when(session.canRegisterExit()).thenReturn(canRegisterExit);
        when(session.calculateParkedHours()).thenReturn(2.0);
        return session;
    }

    private User createMockUser(Long id, String firstName, String lastName) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getFirstname()).thenReturn(firstName);
        when(user.getLastname()).thenReturn(lastName);
        return user;
    }

    private ParkingSpace createMockParkingSpace(Long id, String spaceNumber) {
        ParkingSpace space = mock(ParkingSpace.class);
        when(space.getId()).thenReturn(id);
        when(space.getSpaceNumber()).thenReturn(spaceNumber);
        return space;
    }

    private VehicleType createMockVehicleType(Long id, String name) {
        VehicleType type = mock(VehicleType.class);
        when(type.getId()).thenReturn(id);
        when(type.getName()).thenReturn(name);
        return type;
    }

    private RateConfig createMockRateConfig() {
        RateConfig config = mock(RateConfig.class);
        when(config.getRatePerHour()).thenReturn(BigDecimal.valueOf(5.0));
        when(config.getMinimumChargeHours()).thenReturn(1);
        when(config.getMaximumDailyRate()).thenReturn(BigDecimal.valueOf(40.0));
        return config;
    }
}
