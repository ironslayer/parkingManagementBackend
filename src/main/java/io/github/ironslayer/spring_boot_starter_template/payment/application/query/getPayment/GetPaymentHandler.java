package io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.RequestHandler;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.repository.PaymentRepository;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.exception.PaymentNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler para obtener información de un pago
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetPaymentHandler implements RequestHandler<GetPaymentRequest, GetPaymentResponse> {

    private final PaymentRepository paymentRepository;

    @Override
    public Class<GetPaymentRequest> getRequestType() {
        return GetPaymentRequest.class;
    }

    @Override
    public GetPaymentResponse handle(GetPaymentRequest request) {
        log.debug("Getting payment with request: {}", request);
        
        Payment payment = findPayment(request);
        
        GetPaymentResponse response = mapToResponse(payment);
        
        log.debug("Payment found successfully: {}", response);
        return response;
    }
    
    /**
     * Busca el pago según los criterios del request
     */
    private Payment findPayment(GetPaymentRequest request) {
        if (request.getPaymentId() != null) {
            log.debug("Searching payment by ID: {}", request.getPaymentId());
            return paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> {
                    log.warn("Payment not found with ID: {}", request.getPaymentId());
                    return new PaymentNotFoundException(request.getPaymentId());
                });
        }
        
        if (request.getParkingSessionId() != null) {
            log.debug("Searching payment by parking session ID: {}", request.getParkingSessionId());
            return paymentRepository.findByParkingSessionId(request.getParkingSessionId())
                .orElseThrow(() -> {
                    log.warn("Payment not found for parking session ID: {}", request.getParkingSessionId());
                    return new PaymentNotFoundException("Payment not found for parking session ID: " + request.getParkingSessionId());
                });
        }
        
        log.error("Invalid request: both paymentId and parkingSessionId are null");
        throw new IllegalArgumentException("Either paymentId or parkingSessionId must be provided");
    }
    
    /**
     * Mapea el pago a la respuesta
     */
    private GetPaymentResponse mapToResponse(Payment payment) {
        return new GetPaymentResponse(
            payment.getId(),
            payment.getParkingSessionId(),
            payment.getPaymentMethod().name(),
            payment.getPaymentStatus().name(),
            payment.getTotalAmount(),
            payment.getCreatedAt(),
            payment.getPaidAt()
        );
    }
}
