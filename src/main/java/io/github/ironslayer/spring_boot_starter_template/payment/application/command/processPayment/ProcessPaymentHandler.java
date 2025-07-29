package io.github.ironslayer.spring_boot_starter_template.payment.application.command.processPayment;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.ParkingSessionNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.PaymentAlreadyExistsException;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.PaymentCalculationException;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.repository.PaymentRepository;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.entity.Vehicle;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.exception.VehicleNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.vehicle.domain.port.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Optional;

/**
 * Handler para procesar un pago de una sesión de parqueo.
 * Calcula automáticamente el monto basado en las tarifas configuradas.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProcessPaymentHandler implements RequestHandler<ProcessPaymentRequest, ProcessPaymentResponse> {
    
    private final PaymentRepository paymentRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;
    private final RateConfigRepository rateConfigRepository;
    private final UserRepository userRepository;
    
    @Override
    public Class<ProcessPaymentRequest> getRequestType() {
        return ProcessPaymentRequest.class;
    }
    
    @Override
    public ProcessPaymentResponse handle(ProcessPaymentRequest request) {
        log.info("Processing payment for parking session ID: {}", request.getParkingSessionId());
        
        // 1. Validar entrada
        validateRequest(request);
        
        // 2. Obtener sesión de parqueo
        ParkingSession parkingSession = getParkingSession(request.getParkingSessionId());
        
        // 3. Verificar que no existe ya un pago para esta sesión
        validateNoExistingPayment(request.getParkingSessionId());
        
        // 4. Validar que la sesión esté finalizada
        validateSessionCompleted(parkingSession);
        
        // 5. Obtener vehículo y configuración de tarifas
        Vehicle vehicle = getVehicle(parkingSession.getVehicleId());
        RateConfig rateConfig = getRateConfig(vehicle.getVehicleTypeId());
        
        // 6. Validar operador (importante para auditoría)
        getOperator(request.getOperatorId());
        
        // 7. Calcular monto si no fue proporcionado
        BigDecimal totalAmount = request.getTotalAmount() != null ? 
            request.getTotalAmount() : 
            calculatePaymentAmount(parkingSession, rateConfig);
        
        // 8. Calcular horas estacionado
        BigDecimal hoursParked = calculateHoursParked(parkingSession);
        
        // 9. Crear y guardar el pago
        Payment payment = createPayment(
            request.getParkingSessionId(),
            totalAmount,
            hoursParked,
            rateConfig.getRatePerHour(),
            request.getPaymentMethod(),
            request.getOperatorId()
        );
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // 10. Marcar como pagado inmediatamente
        savedPayment.markAsPaid();
        savedPayment = paymentRepository.save(savedPayment);
        
        log.info("Payment processed successfully with ID: {} for amount: {}", 
                savedPayment.getId(), savedPayment.getTotalAmount());
        
        return ProcessPaymentResponse.success(savedPayment);
    }
    
    private void validateRequest(ProcessPaymentRequest request) {
        if (request.getParkingSessionId() == null) {
            throw new BadRequestException("Parking session ID is required");
        }
        if (request.getPaymentMethod() == null) {
            throw new BadRequestException("Payment method is required");
        }
        if (request.getOperatorId() == null) {
            throw new BadRequestException("Operator ID is required");
        }
        if (request.getTotalAmount() != null && request.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Total amount cannot be negative");
        }
    }
    
    private ParkingSession getParkingSession(Long parkingSessionId) {
        return parkingSessionRepository.findById(parkingSessionId)
                .orElseThrow(() -> new ParkingSessionNotFoundException(parkingSessionId));
    }
    
    private void validateNoExistingPayment(Long parkingSessionId) {
        if (paymentRepository.existsByParkingSessionId(parkingSessionId)) {
            throw new PaymentAlreadyExistsException(parkingSessionId);
        }
    }
    
    private void validateSessionCompleted(ParkingSession parkingSession) {
        if (parkingSession.getExitTime() == null) {
            throw new BadRequestException("Cannot process payment for an active parking session. Please end the session first.");
        }
        if (parkingSession.getIsActive() != null && parkingSession.getIsActive()) {
            throw new BadRequestException("Cannot process payment for an active parking session. Session must be completed first.");
        }
    }
    
    private Vehicle getVehicle(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }
    
    private RateConfig getRateConfig(Long vehicleTypeId) {
        Optional<RateConfig> rateConfigOpt = rateConfigRepository.findActiveByVehicleTypeId(vehicleTypeId);
        if (rateConfigOpt.isEmpty()) {
            throw new PaymentCalculationException("No active rate configuration found for vehicle type ID: " + vehicleTypeId);
        }
        return rateConfigOpt.get();
    }
    
    private User getOperator(Long operatorId) {
        return userRepository.findById(operatorId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + operatorId));
    }
    
    private BigDecimal calculatePaymentAmount(ParkingSession parkingSession, RateConfig rateConfig) {
        try {
            // Calcular horas estacionado
            Duration duration = Duration.between(parkingSession.getEntryTime(), parkingSession.getExitTime());
            BigDecimal hoursParked = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            
            // Aplicar mínimo de horas si es necesario
            BigDecimal minimumHours = BigDecimal.valueOf(rateConfig.getMinimumChargeHours());
            if (hoursParked.compareTo(minimumHours) < 0) {
                hoursParked = minimumHours;
            }
            
            // Calcular monto base (redondear horas hacia arriba para el cobro)
            BigDecimal ceiledHours = BigDecimal.valueOf(Math.ceil(hoursParked.doubleValue()));
            BigDecimal totalAmount = ceiledHours.multiply(rateConfig.getRatePerHour());
            
            // Aplicar tarifa máxima diaria si existe
            if (rateConfig.getMaximumDailyRate() != null && 
                totalAmount.compareTo(rateConfig.getMaximumDailyRate()) > 0) {
                totalAmount = rateConfig.getMaximumDailyRate();
            }
            
            return totalAmount.setScale(2, RoundingMode.HALF_UP);
            
        } catch (Exception e) {
            throw new PaymentCalculationException("Error calculating payment amount: " + e.getMessage(), e);
        }
    }
    
    private BigDecimal calculateHoursParked(ParkingSession parkingSession) {
        Duration duration = Duration.between(parkingSession.getEntryTime(), parkingSession.getExitTime());
        return BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
    
    private Payment createPayment(
            Long parkingSessionId,
            BigDecimal totalAmount,
            BigDecimal hoursParked,
            BigDecimal rateApplied,
            io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentMethod paymentMethod,
            Long operatorId) {
        
        return Payment.createNewPayment(
                parkingSessionId,
                totalAmount,
                hoursParked,
                rateApplied,
                paymentMethod,
                operatorId
        );
    }
}
