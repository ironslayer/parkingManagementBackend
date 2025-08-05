package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.controller;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.common.security.AuthenticatedUserService;
import io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment.ProcessPaymentRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment.ProcessPaymentResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount.CalculateAmountRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount.CalculateAmountResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment.GetPaymentRequest;
import io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment.GetPaymentResponse;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.service.PaymentStatusService;
import io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para operaciones de pagos
 */
@RestController
@RequestMapping("/api/v1/payments")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Payment", description = "The Payment API. Contains all operations for processing payments and calculating amounts for parking sessions.")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentController {

    private final Mediator mediator;
    private final AuthenticatedUserService authenticatedUserService;

    /**
     * Calcular el monto a pagar para una sesión de parqueo
     * Solo ADMIN y OPERATOR pueden calcular montos
     */
    @Operation(summary = "Calculate payment amount", description = "Calculate the payment amount for a parking session based on hours parked and applicable rates (ADMIN and OPERATOR only)")
    @PostMapping("/calculate")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<CalculateAmountResponseDto> calculateAmount(
            @Valid @RequestBody CalculateAmountRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("User {} calculating payment amount for parking session {}", 
                userDetails.getUsername(), requestDto.getParkingSessionId());

        CalculateAmountRequest request = new CalculateAmountRequest(requestDto.getParkingSessionId());
        CalculateAmountResponse response = mediator.dispatch(request);

        CalculateAmountResponseDto responseDto = new CalculateAmountResponseDto(
                response.getParkingSessionId(),
                response.getTotalAmount(),
                response.getHoursParked(),
                response.getRatePerHour()
        );

        log.info("Payment amount calculated successfully for parking session {}: {}", 
                requestDto.getParkingSessionId(), response.getTotalAmount());
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Procesar un pago para una sesión de parqueo
     * Solo ADMIN y OPERATOR pueden procesar pagos
     */
    @Operation(summary = "Process payment", description = "Process a payment for a completed parking session with the specified payment method (ADMIN and OPERATOR only)")
    @PostMapping("/process")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<ProcessPaymentResponseDto> processPayment(
            @Valid @RequestBody ProcessPaymentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("User {} processing payment for parking session {} with method {}", 
                userDetails.getUsername(), requestDto.getParkingSessionId(), requestDto.getPaymentMethod());

        // Obtener el ID del usuario autenticado desde el JWT (más eficiente)
        Long operatorId = authenticatedUserService.getCurrentUserId();
        
        ProcessPaymentRequest request = new ProcessPaymentRequest();
        request.setParkingSessionId(requestDto.getParkingSessionId());
        request.setPaymentMethod(PaymentMethod.valueOf(requestDto.getPaymentMethod().toUpperCase()));
        request.setOperatorId(operatorId); // Ahora usamos el ID real del usuario autenticado desde el JWT
        
        ProcessPaymentResponse response = mediator.dispatch(request);

        ProcessPaymentResponseDto responseDto = new ProcessPaymentResponseDto(
                response.getPayment().getId(),
                response.getPayment().getParkingSessionId(),
                response.getPayment().getTotalAmount(),
                response.getPayment().getHoursParked(),
                response.getPayment().getRateApplied(),
                response.getPayment().getPaymentMethod().name(),
                response.getPayment().getPaymentStatus().name(),
                response.getPayment().getCreatedAt(),
                response.getPayment().getPaidAt()
        );

        log.info("Payment processed successfully with ID: {}", response.getPayment().getId());
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Obtener información de un pago por ID
     * Solo ADMIN y OPERATOR pueden consultar pagos
     */
    @Operation(summary = "Get payment by ID", description = "Retrieve payment information by payment ID (ADMIN and OPERATOR only)")
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<PaymentResponseDto> getPayment(
            @PathVariable @Positive(message = "Payment ID must be positive") Long paymentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("User {} getting payment with ID: {}", userDetails.getUsername(), paymentId);

        GetPaymentRequest request = GetPaymentRequest.byId(paymentId);
        GetPaymentResponse response = mediator.dispatch(request);

        PaymentResponseDto responseDto = new PaymentResponseDto(
                response.getId(),
                response.getParkingSessionId(),
                response.getPaymentMethod(),
                response.getStatus(),
                response.getAmount(),
                response.getCreatedAt(),
                response.getPaidAt()
        );

        log.info("Payment retrieved successfully: {}", paymentId);
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Obtener información de un pago por ID de sesión de parqueo
     * Solo ADMIN y OPERATOR pueden consultar pagos
     */
    @Operation(summary = "Get payment by parking session", description = "Retrieve payment information by parking session ID (ADMIN and OPERATOR only)")
    @GetMapping("/by-session/{parkingSessionId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<PaymentResponseDto> getPaymentBySession(
            @PathVariable @Positive(message = "Parking session ID must be positive") Long parkingSessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("User {} getting payment for parking session: {}", userDetails.getUsername(), parkingSessionId);

        GetPaymentRequest request = GetPaymentRequest.byParkingSessionId(parkingSessionId);
        GetPaymentResponse response = mediator.dispatch(request);

        PaymentResponseDto responseDto = new PaymentResponseDto(
                response.getId(),
                response.getParkingSessionId(),
                response.getPaymentMethod(),
                response.getStatus(),
                response.getAmount(),
                response.getCreatedAt(),
                response.getPaidAt()
        );

        log.info("Payment retrieved successfully for session: {}", parkingSessionId);
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 🚫 Cancel Payment
     * Cancels a pending payment
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(
            summary = "Cancel Payment",
            description = "Cancels a pending payment. Only PENDING payments can be cancelled.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<Map<String, Object>> cancelPayment(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Cancelling payment with ID: {} by user: {}", id, userDetails.getUsername());

        // Get payment to verify it exists and is cancellable
        GetPaymentRequest getRequest = GetPaymentRequest.byId(id);
        GetPaymentResponse getResponse = mediator.dispatch(getRequest);

        if (!"PENDING".equals(getResponse.getStatus())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "PAYMENT_NOT_CANCELLABLE");
            errorResponse.put("message", "Only pending payments can be cancelled");
            errorResponse.put("current_status", getResponse.getStatus());
            errorResponse.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // TODO: Implement CancelPaymentCommand when needed
        // For now, return success response indicating the operation would succeed
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "Payment cancellation requested successfully");
        successResponse.put("payment_id", id);
        successResponse.put("status", "CANCELLATION_PENDING");
        successResponse.put("note", "Actual cancellation logic to be implemented with CancelPaymentCommand");
        successResponse.put("timestamp", LocalDateTime.now());

        log.info("Payment {} marked for cancellation", id);
        
        return ResponseEntity.ok(successResponse);
    }
}
