package io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.entity.ParkingSession;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.exception.ParkingSessionNotFoundException;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.domain.port.ParkingSessionRepository;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.PaymentCalculationException;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.entity.RateConfig;
import io.github.ironslayer.spring_boot_starter_template.rateconfig.domain.port.RateConfigRepository;
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
 * Handler para calcular el monto a pagar de una sesión de parqueo.
 * Útil para mostrar al usuario cuánto debe pagar antes de procesar el pago.
 * ACCESIBLE para ADMIN y OPERATOR
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CalculateAmountHandler implements RequestHandler<CalculateAmountRequest, CalculateAmountResponse> {
    
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;
    private final RateConfigRepository rateConfigRepository;
    
    @Override
    public Class<CalculateAmountRequest> getRequestType() {
        return CalculateAmountRequest.class;
    }
    
    @Override
    public CalculateAmountResponse handle(CalculateAmountRequest request) {
        log.info("Calculating payment amount for parking session ID: {}", request.getParkingSessionId());
        
        // 1. Validar entrada
        if (request.getParkingSessionId() == null) {
            throw new BadRequestException("Parking session ID is required");
        }
        
        // 2. Obtener sesión de parqueo
        ParkingSession parkingSession = parkingSessionRepository.findById(request.getParkingSessionId())
                .orElseThrow(() -> new ParkingSessionNotFoundException(request.getParkingSessionId()));
        
        // 3. Validar que la sesión esté finalizada
        if (parkingSession.getExitTime() == null) {
            throw new BadRequestException("Cannot calculate amount for an active parking session. Please end the session first.");
        }
        
        // 4. Obtener vehículo y configuración de tarifas
        Vehicle vehicle = vehicleRepository.findById(parkingSession.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(parkingSession.getVehicleId()));
        
        Optional<RateConfig> rateConfigOpt = rateConfigRepository.findActiveByVehicleTypeId(vehicle.getVehicleTypeId());
        if (rateConfigOpt.isEmpty()) {
            throw new PaymentCalculationException("No active rate configuration found for vehicle type ID: " + vehicle.getVehicleTypeId());
        }
        
        RateConfig rateConfig = rateConfigOpt.get();
        
        // 5. Calcular monto
        CalculateAmountResponse response = calculateAmount(parkingSession, rateConfig);
        
        log.info("Amount calculated: {} for session ID: {}", response.getTotalAmount(), request.getParkingSessionId());
        
        return response;
    }
    
    private CalculateAmountResponse calculateAmount(ParkingSession parkingSession, RateConfig rateConfig) {
        try {
            // Calcular horas estacionado
            Duration duration = Duration.between(parkingSession.getEntryTime(), parkingSession.getExitTime());
            BigDecimal hoursParked = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            
            // Aplicar mínimo de horas si es necesario
            BigDecimal minimumHours = BigDecimal.valueOf(rateConfig.getMinimumChargeHours());
            BigDecimal chargeableHours = hoursParked.compareTo(minimumHours) < 0 ? minimumHours : hoursParked;
            
            // Calcular monto base (redondear horas hacia arriba para el cobro)
            BigDecimal ceiledHours = BigDecimal.valueOf(Math.ceil(chargeableHours.doubleValue()));
            BigDecimal totalAmount = ceiledHours.multiply(rateConfig.getRatePerHour());
            
            // Aplicar tarifa máxima diaria si existe
            if (rateConfig.getMaximumDailyRate() != null && 
                totalAmount.compareTo(rateConfig.getMaximumDailyRate()) > 0) {
                totalAmount = rateConfig.getMaximumDailyRate();
            }
            
            return new CalculateAmountResponse(
                    totalAmount.setScale(2, RoundingMode.HALF_UP),
                    hoursParked,
                    rateConfig.getRatePerHour(),
                    rateConfig.getMinimumChargeHours(),
                    rateConfig.getMaximumDailyRate(),
                    parkingSession.getId()
            );
            
        } catch (Exception e) {
            throw new PaymentCalculationException("Error calculating payment amount: " + e.getMessage(), e);
        }
    }
}
