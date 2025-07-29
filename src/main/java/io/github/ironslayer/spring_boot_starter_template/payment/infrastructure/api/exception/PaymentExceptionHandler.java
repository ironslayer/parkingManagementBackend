package io.github.ironslayer.spring_boot_starter_template.payment.infrastructure.api.exception;

import io.github.ironslayer.spring_boot_starter_template.exception.domain.*;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🛡️ Global Exception Handler for Payment Module
 * 
 * Provides user-friendly error messages in English for frontend guidance
 * without exposing sensitive system information.
 */
@Slf4j
@RestControllerAdvice(basePackages = "io.github.ironslayer.spring_boot_starter_template.payment")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PaymentExceptionHandler {

    /**
     * 🚫 Payment Not Found
     */
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentNotFound(PaymentNotFoundException ex) {
        log.warn("Payment not found: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "PAYMENT_NOT_FOUND");
        errorResponse.put("message", "The requested payment could not be found");
        errorResponse.put("details", "Please verify the payment ID and try again");
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * 💰 Payment Already Processed
     */
    @ExceptionHandler({BadRequestException.class, PaymentAlreadyExistsException.class})
    public ResponseEntity<Map<String, Object>> handlePaymentAlreadyProcessed(RuntimeException ex) {
        log.warn("Payment processing error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        
        // Determine specific error type based on message content
        if (ex instanceof PaymentAlreadyExistsException || ex.getMessage().toLowerCase().contains("already")) {
            errorResponse.put("error", "PAYMENT_ALREADY_EXISTS");
            errorResponse.put("message", "Payment for this parking session already exists");
            errorResponse.put("details", "Each parking session can only have one payment");
        } else if (ex.getMessage().toLowerCase().contains("session")) {
            errorResponse.put("error", "INVALID_PARKING_SESSION");
            errorResponse.put("message", "The parking session is invalid or not found");
            errorResponse.put("details", "Please verify the session ID is correct and active");
        } else if (ex.getMessage().toLowerCase().contains("method")) {
            errorResponse.put("error", "INVALID_PAYMENT_METHOD");
            errorResponse.put("message", "The payment method provided is not valid");
            errorResponse.put("details", "Accepted methods: CASH, CARD, TRANSFER");
        } else {
            errorResponse.put("error", "PAYMENT_PROCESSING_ERROR");
            errorResponse.put("message", "Unable to process payment request");
            errorResponse.put("details", "Please check your request data and try again");
        }
        
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 🧮 Payment Calculation Error
     */
    @ExceptionHandler(PaymentCalculationException.class)
    public ResponseEntity<Map<String, Object>> handleCalculationError(PaymentCalculationException ex) {
        log.warn("Payment calculation error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "CALCULATION_ERROR");
        errorResponse.put("message", "Unable to calculate payment amount");
        errorResponse.put("details", "Rate configuration may be missing or invalid for this vehicle type");
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 🔐 Access Denied (Remove if ForbiddenException doesn't exist)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied for payment operation: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "ACCESS_DENIED");
        errorResponse.put("message", "You don't have permission to perform this operation");
        errorResponse.put("details", "Contact your administrator if you believe this is an error");
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * ✅ Validation Errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation errors in payment request: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = switch (fieldName) {
                case "parking_session_id" -> "Parking session ID is required and must be a positive number";
                case "payment_method" -> "Payment method is required (CASH, CARD, or TRANSFER)";
                case "amount" -> "Amount must be a positive number";
                default -> error.getDefaultMessage();
            };
            fieldErrors.put(fieldName, errorMessage);
        });
        
        errorResponse.put("error", "VALIDATION_ERROR");
        errorResponse.put("message", "Request data validation failed");
        errorResponse.put("details", "Please correct the following fields and try again");
        errorResponse.put("field_errors", fieldErrors);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 🛠️ Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        log.error("Unexpected error in payment processing: ", ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "INTERNAL_SERVER_ERROR");
        errorResponse.put("message", "An unexpected error occurred while processing your payment");
        errorResponse.put("details", "Please try again later or contact support if the problem persists");
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 💳 Payment Processing Timeout
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentTimeout(RuntimeException ex) {
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("timeout")) {
            log.warn("Payment processing timeout: {}", ex.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "PAYMENT_TIMEOUT");
            errorResponse.put("message", "Payment processing timed out");
            errorResponse.put("details", "The payment request took too long to process. Please try again");
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.REQUEST_TIMEOUT.value());
            
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(errorResponse);
        }
        
        // Re-throw if not a timeout error
        throw ex;
    }
}
