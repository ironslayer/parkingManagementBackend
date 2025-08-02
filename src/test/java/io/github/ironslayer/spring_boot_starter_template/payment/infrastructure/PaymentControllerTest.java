package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.common.security.AuthenticatedUserService;
import io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment.ProcessPaymentRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment.ProcessPaymentResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount.CalculateAmountRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount.CalculateAmountResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment.GetPaymentRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment.GetPaymentResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.controller.PaymentController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Tests para PaymentController usando MockMVC
 */
@WebMvcTest(PaymentController.class)
@DisplayName("Payment Controller API Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Mediator mediator;

    @MockBean
    private AuthenticatedUserService authenticatedUserService;

    private ProcessPaymentResponse processPaymentResponse;
    private CalculateAmountResponse calculateAmountResponse;

    @BeforeEach
    void setUp() {
        // Setup test payment
        Payment testPayment = Payment.builder()
            .id(1L)
            .parkingSessionId(1L)
            .totalAmount(new BigDecimal("5000.00"))
            .hoursParked(new BigDecimal("2.5"))
            .rateApplied(new BigDecimal("2000.00"))
            .paymentMethod(PaymentMethod.CASH)
            .paymentStatus(PaymentStatus.PAID)
            .createdAt(LocalDateTime.now())
            .paidAt(LocalDateTime.now())
            .build();

        // Setup process payment response
        processPaymentResponse = new ProcessPaymentResponse(testPayment, "Payment processed successfully");

        // Setup calculate amount response
        calculateAmountResponse = new CalculateAmountResponse();
        calculateAmountResponse.setParkingSessionId(1L);
        calculateAmountResponse.setTotalAmount(new BigDecimal("5000.00"));
        calculateAmountResponse.setHoursParked(new BigDecimal("2.5"));
        calculateAmountResponse.setRatePerHour(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should process payment successfully with OPERATOR role")
    @WithMockUser(authorities = {"OPERATOR"})
    void shouldProcessPaymentSuccessfully() throws Exception {
        // Given
        when(authenticatedUserService.getCurrentUserId()).thenReturn(1L);
        when(mediator.dispatch(any(ProcessPaymentRequest.class))).thenReturn(processPaymentResponse);

        String requestJson = """
            {
                "parking_session_id": 1,
                "payment_method": "CASH"
            }
        """;

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.parking_session_id").value(1))
                .andExpect(jsonPath("$.payment_method").value("CASH"))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("Should calculate amount successfully")
    @WithMockUser(authorities = {"OPERATOR"})
    void shouldCalculateAmountSuccessfully() throws Exception {
        // Given
        when(mediator.dispatch(any(CalculateAmountRequest.class))).thenReturn(calculateAmountResponse);

        String requestJson = """
            {
                "parking_session_id": 1
            }
        """;

        // When & Then
        mockMvc.perform(post("/api/payments/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_amount").value(5000.00))
                .andExpect(jsonPath("$.hours_parked").value(2.5))
                .andExpect(jsonPath("$.rate_applied").value(2000.00))
                .andExpect(jsonPath("$.parking_session_id").value(1));
    }

    @Test
    @DisplayName("Should get payment by ID successfully")
    @WithMockUser(authorities = {"OPERATOR"})
    void shouldGetPaymentByIdSuccessfully() throws Exception {
        // Given
        GetPaymentResponse getPaymentResponse = new GetPaymentResponse();
        getPaymentResponse.setId(1L);
        getPaymentResponse.setParkingSessionId(1L);
        getPaymentResponse.setAmount(new BigDecimal("5000.00"));
        getPaymentResponse.setPaymentMethod("CASH");
        getPaymentResponse.setStatus("PAID");
        getPaymentResponse.setCreatedAt(LocalDateTime.now());
        getPaymentResponse.setPaidAt(LocalDateTime.now());
        
        when(mediator.dispatch(any(GetPaymentRequest.class))).thenReturn(getPaymentResponse);

        // When & Then
        mockMvc.perform(get("/api/payments/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.payment_method").value("CASH"))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("Should return 403 for unauthorized user")
    void shouldReturn401ForUnauthorizedUser() throws Exception {
        String requestJson = """
            {
                "parking_session_id": 1,
                "payment_method": "CASH"
            }
        """;

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 for CUSTOMER role")
    @WithMockUser(authorities = {"CUSTOMER"})
    void shouldReturn403ForCustomerRole() throws Exception {
        String requestJson = """
            {
                "parking_session_id": 1,
                "payment_method": "CASH"
            }
        """;

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should process payment with ADMIN role")
    @WithMockUser(authorities = {"ADMIN"})
    void shouldProcessPaymentWithAdminRole() throws Exception {
        // Given
        when(authenticatedUserService.getCurrentUserId()).thenReturn(1L);
        when(mediator.dispatch(any(ProcessPaymentRequest.class))).thenReturn(processPaymentResponse);

        String requestJson = """
            {
                "parking_session_id": 1,
                "payment_method": "CASH"
            }
        """;

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should return 400 for invalid request")
    @WithMockUser(authorities = {"OPERATOR"})
    void shouldReturn400ForInvalidRequest() throws Exception {
        String invalidRequestJson = """
            {
                "parking_session_id": -1,
                "payment_method": ""
            }
        """;

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }
}
