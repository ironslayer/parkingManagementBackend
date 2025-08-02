package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoAvailableSpaceException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.VehicleAlreadyParkedException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StartSessionHandler Tests")
class StartSessionHandlerTest {

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

    @InjectMocks
    private StartSessionHandler startSessionHandler;

    @Nested
    @DisplayName("Request Validation Tests")
    class RequestValidationTests {

        @Test
        @DisplayName("Should throw BadRequestException when operator ID is null")
        void shouldThrowBadRequestExceptionWhenOperatorIdIsNull() {
            // Given
            StartSessionRequest request = new StartSessionRequest("ABC123", null);

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). Operator ID is required");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                parkingSpaceRepository, userRepository, vehicleTypeRepository);
        }

        @Test
        @DisplayName("Should throw BadRequestException when license plate is null")
        void shouldThrowBadRequestExceptionWhenLicensePlateIsNull() {
            // Given
            StartSessionRequest request = new StartSessionRequest(null, 1L);

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). License plate is required");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                parkingSpaceRepository, userRepository, vehicleTypeRepository);
        }

        @Test
        @DisplayName("Should throw BadRequestException when license plate is empty")
        void shouldThrowBadRequestExceptionWhenLicensePlateIsEmpty() {
            // Given
            StartSessionRequest request = new StartSessionRequest("   ", 1L);

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Bad Request Exception (400). License plate is required");

            verifyNoInteractions(parkingSessionRepository, vehicleRepository, 
                                parkingSpaceRepository, userRepository, vehicleTypeRepository);
        }
    }

    @Nested
    @DisplayName("Vehicle Validation Tests")
    class VehicleValidationTests {

        @Test
        @DisplayName("Should throw VehicleNotFoundException when vehicle does not exist")
        void shouldThrowVehicleNotFoundExceptionWhenVehicleDoesNotExist() {
            // Given
            StartSessionRequest request = new StartSessionRequest("INVALID123", 1L);
            
            when(vehicleRepository.findByLicensePlate("INVALID123"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(VehicleNotFoundException.class);

            verify(vehicleRepository).findByLicensePlate("INVALID123");
            verifyNoInteractions(parkingSessionRepository, parkingSpaceRepository, 
                                userRepository, vehicleTypeRepository);
        }

        @Test
        @DisplayName("Should normalize license plate to uppercase")
        void shouldNormalizeLicensePlateToUppercase() {
            // Given
            StartSessionRequest request = new StartSessionRequest("abc123", 1L);
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(VehicleNotFoundException.class);

            verify(vehicleRepository).findByLicensePlate("ABC123");
        }
    }

    @Nested
    @DisplayName("Active Session Validation Tests")
    class ActiveSessionValidationTests {

        @Test
        @DisplayName("Should throw VehicleAlreadyParkedException when vehicle has active session")
        void shouldThrowVehicleAlreadyParkedExceptionWhenVehicleHasActiveSession() {
            // Given
            StartSessionRequest request = new StartSessionRequest("ABC123", 1L);
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.hasActiveSession(1L))
                    .thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(VehicleAlreadyParkedException.class);

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).hasActiveSession(1L);
            verifyNoInteractions(parkingSpaceRepository, userRepository, vehicleTypeRepository);
        }
    }

    @Nested
    @DisplayName("Parking Space Availability Tests")
    class ParkingSpaceAvailabilityTests {

        @Test
        @DisplayName("Should throw NoAvailableSpaceException when no spaces available")
        void shouldThrowNoAvailableSpaceExceptionWhenNoSpacesAvailable() {
            // Given
            StartSessionRequest request = new StartSessionRequest("ABC123", 1L);
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            VehicleType vehicleType = createMockVehicleType(1L, "CAR");
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.hasActiveSession(1L))
                    .thenReturn(false);
            when(parkingSpaceRepository.findAllAvailableByVehicleType(1L))
                    .thenReturn(List.of()); // No spaces available
            when(vehicleTypeRepository.findById(1L))
                    .thenReturn(Optional.of(vehicleType));

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(NoAvailableSpaceException.class);

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).hasActiveSession(1L);
            verify(parkingSpaceRepository).findAllAvailableByVehicleType(1L);
            verify(vehicleTypeRepository).findById(1L);
            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("Operator Validation Tests")
    class OperatorValidationTests {

        @Test
        @DisplayName("Should throw UserNotFoundException when operator does not exist")
        void shouldThrowUserNotFoundExceptionWhenOperatorDoesNotExist() {
            // Given
            StartSessionRequest request = new StartSessionRequest("ABC123", 999L);
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            ParkingSpace parkingSpace = createMockParkingSpace(1L, "A01");
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.hasActiveSession(1L))
                    .thenReturn(false);
            when(parkingSpaceRepository.findAllAvailableByVehicleType(1L))
                    .thenReturn(List.of(parkingSpace));
            when(userRepository.findById(999L))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> startSessionHandler.handle(request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User with ID '999' not found");

            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).hasActiveSession(1L);
            verify(parkingSpaceRepository).findAllAvailableByVehicleType(1L);
            verify(userRepository).findById(999L);
            verifyNoInteractions(vehicleTypeRepository);
        }
    }

    @Nested
    @DisplayName("Successful Session Creation Tests")
    class SuccessfulSessionCreationTests {

        @Test
        @DisplayName("Should create parking session successfully with valid data")
        void shouldCreateParkingSessionSuccessfullyWithValidData() {
            // Given
            StartSessionRequest request = new StartSessionRequest("ABC123", 1L);
            Vehicle vehicle = createMockVehicle(1L, "ABC123", 1L);
            ParkingSpace parkingSpace = createMockParkingSpace(1L, "A01");
            User operator = createMockUser(1L, "John", "Doe");
            VehicleType vehicleType = createMockVehicleType(1L, "CAR");
            ParkingSession savedSession = createMockParkingSession(1L);
            
            when(vehicleRepository.findByLicensePlate("ABC123"))
                    .thenReturn(Optional.of(vehicle));
            when(parkingSessionRepository.hasActiveSession(1L))
                    .thenReturn(false);
            when(parkingSpaceRepository.findAllAvailableByVehicleType(1L))
                    .thenReturn(List.of(parkingSpace));
            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(operator));
            when(parkingSessionRepository.save(any(ParkingSession.class)))
                    .thenReturn(savedSession);
            when(parkingSpaceRepository.save(any(ParkingSpace.class)))
                    .thenReturn(parkingSpace);
            when(vehicleTypeRepository.findById(1L))
                    .thenReturn(Optional.of(vehicleType));

            // When
            StartSessionResponse response = startSessionHandler.handle(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.sessionId()).isEqualTo(1L);
            assertThat(response.licensePlate()).isEqualTo("ABC123");
            assertThat(response.vehicleType()).isEqualTo("CAR");
            assertThat(response.assignedSpace()).isEqualTo("A01");
            assertThat(response.operatorName()).isEqualTo("John Doe");
            assertThat(response.ticketCode()).isNotBlank();
            assertThat(response.entryTime()).isNotBlank();

            // Verify interactions
            verify(vehicleRepository).findByLicensePlate("ABC123");
            verify(parkingSessionRepository).hasActiveSession(1L);
            verify(parkingSpaceRepository).findAllAvailableByVehicleType(1L);
            verify(userRepository).findById(1L);
            verify(parkingSessionRepository, times(2)).save(any(ParkingSession.class));
            verify(parkingSpaceRepository).save(parkingSpace);
            verify(vehicleTypeRepository).findById(1L);
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

    private VehicleType createMockVehicleType(Long id, String name) {
        VehicleType type = mock(VehicleType.class);
        when(type.getId()).thenReturn(id);
        when(type.getName()).thenReturn(name);
        return type;
    }

    private ParkingSession createMockParkingSession(Long id) {
        ParkingSession session = mock(ParkingSession.class);
        when(session.getId()).thenReturn(id);
        when(session.getTicketCode()).thenReturn("T-202501311200-001");
        when(session.getEntryTime()).thenReturn(LocalDateTime.now());
        return session;
    }
}
