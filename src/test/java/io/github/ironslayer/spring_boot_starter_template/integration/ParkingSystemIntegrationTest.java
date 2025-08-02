package io.github.ironslayer.spring_boot_starter_template.integration;

import io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.create.CreateVehicleRequest;
import io.github.ironslayer.spring_boot_starter_template.vehicle.application.command.create.CreateVehicleResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession.StartSessionRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession.StartSessionResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession.EndSessionRequest;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.endSession.EndSessionResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment.ProcessPaymentRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment.ProcessPaymentResponse;
import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

/**
 * Test de integración que verifica el flujo completo del sistema de parqueo
 * usando TestContainers para PostgreSQL y Redis reales
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Integration Tests - Complete Parking System Flow")
class ParkingSystemIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("parking_test")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL configuration
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        // Redis configuration
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        
        // JPA configuration for testing
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private Mediator mediator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setupTestData() {
        try {
            // Insert Vehicle Types
            jdbcTemplate.execute("INSERT INTO vehicle_types (id, name, description, is_active, created_at) VALUES " +
                    "(1, 'CAR', 'Automóviles y sedanes', true, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING");
            
            jdbcTemplate.execute("INSERT INTO vehicle_types (id, name, description, is_active, created_at) VALUES " +
                    "(2, 'MOTORCYCLE', 'Motocicletas y ciclomotores', true, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING");
            
            // Insert Rate Configs
            jdbcTemplate.execute("INSERT INTO rate_configs (id, vehicle_type_id, rate_per_hour, minimum_charge_hours, maximum_daily_rate, is_active, created_at, updated_at) VALUES " +
                    "(1, 1, 2000.00, 1, 15000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING");
            
            // Insert Parking Spaces (using actual columns from ParkingSpace entity)
            jdbcTemplate.execute("INSERT INTO parking_spaces (id, space_number, vehicle_type_id, is_occupied, is_active, created_at, updated_at) VALUES " +
                    "(1, 'A-001', 1, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING");
            
            // Insert Test User (Operator)
            jdbcTemplate.execute("INSERT INTO users (id, firstname, lastname, email, password, role, is_active, created_at, updated_at) VALUES " +
                    "(1, 'Test', 'Operator', 'test@operator.com', '$2a$10$obirjZMfHwFQpk1giGHaJenIIrS6n1oTA//GL.PcZPRqy0w0y23Em', 'OPERATOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING");
            
        } catch (Exception e) {
            System.err.println("Failed to setup test data: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to setup test data", e);
        }
    }

    @BeforeEach
    void setUp() {
        // Verificar que los contenedores están funcionando
        assertThat(postgres.isRunning()).isTrue();
        assertThat(redis.isRunning()).isTrue();
    }

        @Test
    @DisplayName("Should complete full parking flow: Vehicle Registration → Entry → Exit → Payment")
    void shouldCompleteFullParkingFlow() {
        // GIVEN - Vehicle data
        String licensePlate = "ABC123";
        Vehicle vehicle = new Vehicle(
            licensePlate,
            1L,        // vehicleTypeId (assuming it exists)
            "Toyota",  // brand
            "Corolla", // model
            "Blue",    // color
            "John Doe", // ownerName
            "555-1234"  // ownerPhone
        );

        // WHEN - Step 1: Register vehicle
        CreateVehicleRequest createVehicleRequest = new CreateVehicleRequest(vehicle);
        CreateVehicleResponse vehicleResponse = mediator.dispatch(createVehicleRequest);
        
        // THEN - Vehicle created successfully
        assertThat(vehicleResponse).isNotNull();
        assertThat(vehicleResponse.vehicleId()).isNotNull();

        // WHEN - Step 2: Start parking session
        StartSessionRequest startRequest = new StartSessionRequest(
            licensePlate,
            1L // operatorId (assuming it exists)
        );
        StartSessionResponse startResponse = mediator.dispatch(startRequest);

        // THEN - Session started successfully
        assertThat(startResponse).isNotNull();
        assertThat(startResponse.sessionId()).isNotNull();
        assertThat(startResponse.ticketCode()).isNotBlank();
        assertThat(startResponse.licensePlate()).isEqualTo(licensePlate);
        assertThat(startResponse.entryTime()).isNotNull();

        // WHEN - Step 3: End parking session
        EndSessionRequest endRequest = new EndSessionRequest(licensePlate, 1L);
        EndSessionResponse endResponse = mediator.dispatch(endRequest);

        // THEN - Session ended successfully
        assertThat(endResponse).isNotNull();
        assertThat(endResponse.sessionId()).isEqualTo(startResponse.sessionId());
        assertThat(endResponse.licensePlate()).isEqualTo(licensePlate);
        assertThat(endResponse.exitTime()).isNotNull();
        assertThat(endResponse.totalAmount()).isGreaterThan(BigDecimal.ZERO);

        // WHEN - Step 4: Process payment
        ProcessPaymentRequest paymentRequest = new ProcessPaymentRequest();
        paymentRequest.setParkingSessionId(endResponse.sessionId());
        paymentRequest.setPaymentMethod(PaymentMethod.CASH);
        paymentRequest.setOperatorId(1L);
        ProcessPaymentResponse paymentResponse = mediator.dispatch(paymentRequest);

        // THEN - Payment processed successfully
        assertThat(paymentResponse).isNotNull();
        assertThat(paymentResponse.getPayment()).isNotNull();
        assertThat(paymentResponse.getPayment().getId()).isNotNull();
        assertThat(paymentResponse.getPayment().getTotalAmount()).isEqualTo(endResponse.totalAmount());
        assertThat(paymentResponse.getPayment().getPaymentMethod()).isEqualTo(PaymentMethod.CASH);
        assertThat(paymentResponse.getPayment().getPaidAt()).isNotNull();

        // FINAL VERIFICATION - Complete flow successful
        assertThat(paymentResponse.getPayment().getTotalAmount()).isEqualTo(endResponse.totalAmount());
    }
}
