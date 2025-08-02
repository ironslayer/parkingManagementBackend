package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getSessionByVehicle;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoActiveSessionException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GetSessionByVehicleHandler Tests")
class GetSessionByVehicleHandlerTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private VehicleTypeRepository vehicleTypeRepository;
    
    @Mock
    private ParkingSpaceRepository parkingSpaceRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RateConfigRepository rateConfigRepository;

    @InjectMocks
    private GetSessionByVehicleHandler getSessionByVehicleHandler;

    @Nested
    @DisplayName("Request Validation Tests")
    class RequestValidationTests {

        @Test
        @DisplayName("Should throw BadRequestException when license plate is null")
        void shouldThrowBadRequestExceptionWhenLicensePlateIsNull() {
            // Given
            GetSessionByVehicleRequest request = new GetSessionByVehicleRequest(null);

            // When & Then
            assertThatThrownBy(() -> getSessionByVehicleHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). License plate is required");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                vehicleTypeRepository, parkingSpaceRepository, userRepository, rateConfigRepository);
        }

        @Test
        @DisplayName("Should throw BadRequestException when license plate is empty")
        void shouldThrowBadRequestExceptionWhenLicensePlateIsEmpty() {
            // Given
            GetSessionByVehicleRequest request = new GetSessionByVehicleRequest("   ");

            // When & Then
            assertThatThrownBy(() -> getSessionByVehicleHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). License plate is required");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                vehicleTypeRepository, parkingSpaceRepository, userRepository, rateConfigRepository);
        }
    }

    @Nested
    @DisplayName("Vehicle Validation Tests")
    class VehicleValidationTests {

        @Test
        @DisplayName("Should throw VehicleNotFoundException when vehicle does not exist")
        void shouldThrowVehicleNotFoundExceptionWhenVehicleDoesNotExist() {
            // Given
            GetSessionByVehicleRequest request = new GetSessionByVehicleRequest("INVALID123");
            
            when(vehicleRepository.findByLicensePlate("INVALID123"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> getSessionByVehicleHandler.handle(request))
                    .isInstanceOf(VehicleNotFoundException.class)
                    .hasMessage("INVALID123");

            verify(vehicleRepository).findByLicensePlate("INVALID123");
            verifyNoInteractions(parkingSessionRepository, vehicleTypeRepository, 
                                parkingSpaceRepository, userRepository, rateConfigRepository);
        }

        @Test
        @DisplayName("Should normalize license plate to uppercase")
        void shouldNormalizeLicensePlateToUppercase() {
            // Given
            GetSessionByVehicleRequest request = new GetSessionByVehicleRequest("abc123");
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> getSessionByVehicleHandler.handle(request))
                    .isInstanceOf(VehicleNotFoundException.class);

            verify(vehicleRepository).findByLicensePlate("ABC123");
        }
    }

    @Nested
    @DisplayName("Active Session Validation Tests")
    class ActiveSessionValidationTests {

        @Test
        @DisplayName("Should throw NoActiveSessionException when vehicle has no active session")
        void shouldThrowNoActiveSessionExceptionWhenVehicleHasNoActiveSession() {
            // Given
            GetSessionByVehicleRequest request = new GetSessionByVehicleRequest("ABC123");
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.findActiveSessionByVehicleId(1L))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> getSessionByVehicleHandler.handle(request))
                    .isInstanceOf(NoActiveSessionException.class)
                    .hasMessage("No active parking session found for vehicle with license plate 'ABC123'");

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).findActiveSessionByVehicleId(1L);
            verifyNoInteractions(vehicleTypeRepository, parkingSpaceRepository, userRepository, rateConfigRepository);
        }
    }

    @Nested
    @DisplayName("Successful Query Tests")
    class SuccessfulQueryTests {

        @Test
        @DisplayName("Should return session information successfully with all data")
        void shouldReturnSessionInformationSuccessfullyWithAllData() {
            // Given
            GetSessionByVehicleRequest request = new GetSessionByVehicleRequest("ABC123");
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            ParkingSession activeSession = createMockParkingSession(1L, 1L, 1L, 1L);
            VehicleType vehicleType = createMockVehicleType(1L, "CAR");
            ParkingSpace parkingSpace = createMockParkingSpace(1L, "A01");
            User operator = createMockUser(1L, "John", "Doe");
            RateConfig rateConfig = createMockRateConfig();
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.findActiveSessionByVehicleId(1L))
                    .thenReturn(Optional.of(activeSession));
            when(vehicleTypeRepository.findById(1L))
                    .thenReturn(Optional.of(vehicleType));
            when(parkingSpaceRepository.findById(1L))
                    .thenReturn(Optional.of(parkingSpace));
            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(operator));
            when(rateConfigRepository.findActiveByVehicleTypeId(1L))
                    .thenReturn(Optional.of(rateConfig));
            when(activeSession.calculateParkedHours()).thenReturn(2.5);
            when(activeSession.generateTicketCode()).thenReturn("T-202401011200-001");

            // When
            GetSessionByVehicleResponse response = getSessionByVehicleHandler.handle(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.sessionId()).isEqualTo(1L);
            assertThat(response.licensePlate()).isEqualTo("ABC123");
            assertThat(response.vehicleType()).isEqualTo("CAR");
            assertThat(response.parkingSpace()).isEqualTo("A01");
            assertThat(response.operatorName()).isEqualTo("John Doe");
            assertThat(response.entryTime()).isNotBlank();
            assertThat(response.hoursParked()).isEqualTo(BigDecimal.valueOf(2.5).setScale(2));
            assertThat(response.estimatedAmount()).isNotNull();
            assertThat(response.ticketCode()).isEqualTo("T-202401011200-001");

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).findActiveSessionByVehicleId(1L);
            verify(vehicleTypeRepository).findById(1L);
            verify(parkingSpaceRepository).findById(1L);
            verify(userRepository).findById(1L);
            verify(rateConfigRepository).findActiveByVehicleTypeId(1L);
        }

        @Test
        @DisplayName("Should handle missing optional entities gracefully")
        void shouldHandleMissingOptionalEntitiesGracefully() {
            // Given
            GetSessionByVehicleRequest request = new GetSessionByVehicleRequest("ABC123");
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            ParkingSession activeSession = createMockParkingSession(1L, 1L, 1L, 1L);
            RateConfig rateConfig = createMockRateConfig();
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.findActiveSessionByVehicleId(1L))
                    .thenReturn(Optional.of(activeSession));
            when(vehicleTypeRepository.findById(1L))
                    .thenReturn(Optional.empty()); // Missing vehicle type
            when(parkingSpaceRepository.findById(1L))
                    .thenReturn(Optional.empty()); // Missing parking space
            when(userRepository.findById(1L))
                    .thenReturn(Optional.empty()); // Missing operator
            when(rateConfigRepository.findActiveByVehicleTypeId(1L))
                    .thenReturn(Optional.of(rateConfig));
            when(activeSession.calculateParkedHours()).thenReturn(1.5);
            when(activeSession.generateTicketCode()).thenReturn("T-202401011200-001");

            // When
            GetSessionByVehicleResponse response = getSessionByVehicleHandler.handle(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.sessionId()).isEqualTo(1L);
            assertThat(response.licensePlate()).isEqualTo("ABC123");
            assertThat(response.vehicleType()).isEqualTo("Unknown");
            assertThat(response.parkingSpace()).isEqualTo("Unknown");
            assertThat(response.operatorName()).isEqualTo("Unknown");
            assertThat(response.hoursParked()).isEqualTo(BigDecimal.valueOf(1.5).setScale(2));
            assertThat(response.estimatedAmount()).isNotNull();
            assertThat(response.ticketCode()).isEqualTo("T-202401011200-001");

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).findActiveSessionByVehicleId(1L);
            verify(vehicleTypeRepository).findById(1L);
            verify(parkingSpaceRepository).findById(1L);
            verify(userRepository).findById(1L);
            verify(rateConfigRepository).findActiveByVehicleTypeId(1L);
        }
    }

    @Nested
    @DisplayName("Request Type Tests")
    class RequestTypeTests {

        @Test
        @DisplayName("Should return correct request type")
        void shouldReturnCorrectRequestType() {
            // When
            Class<GetSessionByVehicleRequest> requestType = getSessionByVehicleHandler.getRequestType();

            // Then
            assertThat(requestType).isEqualTo(GetSessionByVehicleRequest.class);
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

    private ParkingSession createMockParkingSession(Long id, Long vehicleId, Long spaceId, Long operatorId) {
        ParkingSession session = mock(ParkingSession.class);
        when(session.getId()).thenReturn(id);
        when(session.getVehicleId()).thenReturn(vehicleId);
        when(session.getParkingSpaceId()).thenReturn(spaceId);
        when(session.getOperatorEntryId()).thenReturn(operatorId);
        when(session.getEntryTime()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        return session;
    }

    private VehicleType createMockVehicleType(Long id, String name) {
        VehicleType type = mock(VehicleType.class);
        when(type.getId()).thenReturn(id);
        when(type.getName()).thenReturn(name);
        return type;
    }

    private ParkingSpace createMockParkingSpace(Long id, String spaceNumber) {
        ParkingSpace space = mock(ParkingSpace.class);
        when(space.getId()).thenReturn(id);
        when(space.getSpaceNumber()).thenReturn(spaceNumber);
        return space;
    }

    private User createMockUser(Long id, String firstName, String lastName) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getFirstname()).thenReturn(firstName);
        when(user.getLastname()).thenReturn(lastName);
        return user;
    }

    private RateConfig createMockRateConfig() {
        RateConfig config = mock(RateConfig.class);
        when(config.getRatePerHour()).thenReturn(BigDecimal.valueOf(5.0));
        when(config.getMinimumChargeHours()).thenReturn(1);
        when(config.getMaximumDailyRate()).thenReturn(BigDecimal.valueOf(40.0));
        return config;
    }
}
