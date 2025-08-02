package io.github.ironslayer.spring_boot_starter_template.parkingsession.application.query.getActiveSessions;

import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpace;
import io.github.ironslayer.spring_boot_starter_template.parkingspace.domain.ParkingSpaceRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GetActiveSessionsHandler Tests")
class GetActiveSessionsHandlerTest {

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

    @InjectMocks
    private GetActiveSessionsHandler getActiveSessionsHandler;

    @Nested
    @DisplayName("Query Execution Tests")
    class QueryExecutionTests {

        @Test
        @DisplayName("Should return empty list when no active sessions exist")
        void shouldReturnEmptyListWhenNoActiveSessionsExist() {
            // Given
            GetActiveSessionsRequest request = new GetActiveSessionsRequest();
            
            when(parkingSessionRepository.findAllActiveSessions())
                    .thenReturn(List.of());

            // When
            GetActiveSessionsResponse response = getActiveSessionsHandler.handle(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.activeSessions()).isEmpty();
            assertThat(response.totalCount()).isEqualTo(0);

            verify(parkingSessionRepository).findAllActiveSessions();
            verifyNoInteractions(vehicleRepository, vehicleTypeRepository, parkingSpaceRepository, userRepository);
        }

        @Test
        @DisplayName("Should return active sessions with complete information")
        void shouldReturnActiveSessionsWithCompleteInformation() {
            // Given
            GetActiveSessionsRequest request = new GetActiveSessionsRequest();
            
            ParkingSession session1 = createMockParkingSession(1L, 1L, 1L, 1L);
            ParkingSession session2 = createMockParkingSession(2L, 2L, 2L, 2L);
            List<ParkingSession> activeSessions = List.of(session1, session2);
            
            Vehicle vehicle1 = createMockVehicle(1L, "ABC123", 1L);
            Vehicle vehicle2 = createMockVehicle(2L, "XYZ789", 2L);
            VehicleType vehicleType1 = createMockVehicleType(1L, "CAR");
            VehicleType vehicleType2 = createMockVehicleType(2L, "MOTORCYCLE");
            ParkingSpace space1 = createMockParkingSpace(1L, "A01");
            ParkingSpace space2 = createMockParkingSpace(2L, "B02");
            User operator1 = createMockUser(1L, "John", "Doe");
            User operator2 = createMockUser(2L, "Jane", "Smith");
            
            when(parkingSessionRepository.findAllActiveSessions())
                    .thenReturn(activeSessions);
            
            // Mock vehicle lookups
            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle1));
            when(vehicleRepository.findById(2L)).thenReturn(Optional.of(vehicle2));
            
            // Mock vehicle type lookups
            when(vehicleTypeRepository.findById(1L)).thenReturn(Optional.of(vehicleType1));
            when(vehicleTypeRepository.findById(2L)).thenReturn(Optional.of(vehicleType2));
            
            // Mock parking space lookups
            when(parkingSpaceRepository.findById(1L)).thenReturn(Optional.of(space1));
            when(parkingSpaceRepository.findById(2L)).thenReturn(Optional.of(space2));
            
            // Mock operator lookups
            when(userRepository.findById(1L)).thenReturn(Optional.of(operator1));
            when(userRepository.findById(2L)).thenReturn(Optional.of(operator2));

            // When
            GetActiveSessionsResponse response = getActiveSessionsHandler.handle(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.activeSessions()).hasSize(2);
            assertThat(response.totalCount()).isEqualTo(2);

            // Verify first session
            GetActiveSessionsResponse.ActiveSessionDTO firstSession = response.activeSessions().get(0);
            assertThat(firstSession.sessionId()).isEqualTo(1L);
            assertThat(firstSession.licensePlate()).isEqualTo("ABC123");
            assertThat(firstSession.vehicleType()).isEqualTo("CAR");
            assertThat(firstSession.parkingSpace()).isEqualTo("A01");
            assertThat(firstSession.operatorName()).isEqualTo("John Doe");
            assertThat(firstSession.entryTime()).isNotBlank();
            assertThat(firstSession.hoursParked()).isNotBlank();

            // Verify second session
            GetActiveSessionsResponse.ActiveSessionDTO secondSession = response.activeSessions().get(1);
            assertThat(secondSession.sessionId()).isEqualTo(2L);
            assertThat(secondSession.licensePlate()).isEqualTo("XYZ789");
            assertThat(secondSession.vehicleType()).isEqualTo("MOTORCYCLE");
            assertThat(secondSession.parkingSpace()).isEqualTo("B02");
            assertThat(secondSession.operatorName()).isEqualTo("Jane Smith");

            // Verify all repository interactions
            verify(parkingSessionRepository).findAllActiveSessions();
            verify(vehicleRepository).findById(1L);
            verify(vehicleRepository).findById(2L);
            verify(vehicleTypeRepository).findById(1L);
            verify(vehicleTypeRepository).findById(2L);
            verify(parkingSpaceRepository).findById(1L);
            verify(parkingSpaceRepository).findById(2L);
            verify(userRepository).findById(1L);
            verify(userRepository).findById(2L);
        }

        @Test
        @DisplayName("Should handle missing related entities gracefully")
        void shouldHandleMissingRelatedEntitiesGracefully() {
            // Given
            GetActiveSessionsRequest request = new GetActiveSessionsRequest();
            
            ParkingSession session = createMockParkingSession(1L, 1L, 1L, 1L);
            List<ParkingSession> activeSessions = List.of(session);
            
            when(parkingSessionRepository.findAllActiveSessions())
                    .thenReturn(activeSessions);
            
            // Mock missing entities (return empty optionals)
            when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());
            when(vehicleTypeRepository.findById(1L)).thenReturn(Optional.empty());
            when(parkingSpaceRepository.findById(1L)).thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // When
            GetActiveSessionsResponse response = getActiveSessionsHandler.handle(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.activeSessions()).hasSize(1);
            assertThat(response.totalCount()).isEqualTo(1);

            GetActiveSessionsResponse.ActiveSessionDTO sessionDTO = response.activeSessions().get(0);
            assertThat(sessionDTO.sessionId()).isEqualTo(1L);
            assertThat(sessionDTO.licensePlate()).isEqualTo("Unknown");
            assertThat(sessionDTO.vehicleType()).isEqualTo("Unknown");
            assertThat(sessionDTO.parkingSpace()).isEqualTo("Unknown");
            assertThat(sessionDTO.operatorName()).isEqualTo("Unknown");

            verify(parkingSessionRepository).findAllActiveSessions();
            verify(vehicleRepository).findById(1L);
            verify(parkingSpaceRepository).findById(1L);
            verify(userRepository).findById(1L);
            // vehicleTypeRepository is not called because vehicle is null
        }
    }

    @Nested
    @DisplayName("Request Type Tests")
    class RequestTypeTests {

        @Test
        @DisplayName("Should return correct request type")
        void shouldReturnCorrectRequestType() {
            // When
            Class<GetActiveSessionsRequest> requestType = getActiveSessionsHandler.getRequestType();

            // Then
            assertThat(requestType).isEqualTo(GetActiveSessionsRequest.class);
        }
    }

    // Helper methods for creating test objects
    private ParkingSession createMockParkingSession(Long id, Long vehicleId, Long spaceId, Long operatorId) {
        ParkingSession session = mock(ParkingSession.class);
        when(session.getId()).thenReturn(id);
        when(session.getVehicleId()).thenReturn(vehicleId);
        when(session.getParkingSpaceId()).thenReturn(spaceId);
        when(session.getOperatorEntryId()).thenReturn(operatorId);
        when(session.getEntryTime()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(session.calculateParkedHours()).thenReturn(2.5);
        return session;
    }

    private Vehicle createMockVehicle(Long id, String licensePlate, Long vehicleTypeId) {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getId()).thenReturn(id);
        when(vehicle.getLicensePlate()).thenReturn(licensePlate);
        when(vehicle.getVehicleTypeId()).thenReturn(vehicleTypeId);
        return vehicle;
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
}
