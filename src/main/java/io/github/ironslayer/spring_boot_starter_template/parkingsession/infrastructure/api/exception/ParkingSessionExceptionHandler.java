package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api.exception;

import io.github.ironslayer.spring_boot_starter_template.common.dto.ErrorResponse;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoAvailableSpaceException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.VehicleAlreadyParkedException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.ParkingSessionNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.NoActiveSessionException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para el módulo de parking sessions.
 * Convierte las excepciones de dominio en respuestas HTTP apropiadas
 * con mensajes descriptivos en inglés.
 */
@Slf4j
@RestControllerAdvice(basePackages = "io.github.ironslayer.spring_boot_starter_template.parkingsession")
@Order(1) // Mayor prioridad que el manejador global general
public class ParkingSessionExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotFound(
            VehicleNotFoundException ex, 
            HttpServletRequest request) {
        
        log.warn("Vehicle not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Vehicle not found")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .errorType("VEHICLE_NOT_FOUND")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(VehicleAlreadyParkedException.class)
    public ResponseEntity<ErrorResponse> handleVehicleAlreadyParked(
            VehicleAlreadyParkedException ex, 
            HttpServletRequest request) {
        
        log.warn("Vehicle already parked: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("Vehicle already has an active parking session")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .errorType("VEHICLE_ALREADY_PARKED")
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(NoAvailableSpaceException.class)
    public ResponseEntity<ErrorResponse> handleNoAvailableSpace(
            NoAvailableSpaceException ex, 
            HttpServletRequest request) {
        
        log.warn("No available parking space: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("No available parking spaces")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .errorType("NO_AVAILABLE_SPACE")
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, 
            HttpServletRequest request) {
        
        log.warn("User/Operator not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Operator not found")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .errorType("OPERATOR_NOT_FOUND")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ParkingSessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleParkingSessionNotFound(
            ParkingSessionNotFoundException ex, 
            HttpServletRequest request) {
        
        log.warn("Parking session not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Parking session not found")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .errorType("SESSION_NOT_FOUND")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NoActiveSessionException.class)
    public ResponseEntity<ErrorResponse> handleNoActiveSession(
            NoActiveSessionException ex, 
            HttpServletRequest request) {
        
        log.warn("No active session found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("No active parking session found")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .errorType("NO_ACTIVE_SESSION")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex, 
            HttpServletRequest request) {
        
        log.warn("Bad request: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid request")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .errorType("BAD_REQUEST")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        StringBuilder details = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            details.append(error.getField())
                   .append(" - ")
                   .append(error.getDefaultMessage())
                   .append("; ")
        );
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid request data")
                .details(details.toString())
                .path(request.getRequestURI())
                .errorType("VALIDATION_ERROR")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex, 
            HttpServletRequest request) {
        
        log.error("Unexpected error in parking session operation", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal server error occurred while processing parking session")
                .details("An unexpected error occurred. Please try again or contact support.")
                .path(request.getRequestURI())
                .errorType("INTERNAL_ERROR")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
