package io.github.ironslayer.spring_boot_starter_template.payment.application.query.getPayment;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para obtener un pago específico
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPaymentRequest implements Request<GetPaymentResponse> {
    
    /**
     * ID del pago a buscar
     */
    private Long paymentId;
    
    /**
     * ID de la sesión de parqueo para buscar su pago
     */
    private Long parkingSessionId;
    
    /**
     * Constructor para buscar por ID de pago
     */
    public static GetPaymentRequest byId(Long paymentId) {
        GetPaymentRequest request = new GetPaymentRequest();
        request.setPaymentId(paymentId);
        return request;
    }
    
    /**
     * Constructor para buscar por ID de sesión
     */
    public static GetPaymentRequest byParkingSessionId(Long parkingSessionId) {
        GetPaymentRequest request = new GetPaymentRequest();
        request.setParkingSessionId(parkingSessionId);
        return request;
    }
}
