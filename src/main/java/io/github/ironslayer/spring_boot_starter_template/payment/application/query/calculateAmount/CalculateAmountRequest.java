package io.github.ironslayer.spring_boot_starter_template.payment.application.query.calculateAmount;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para calcular el monto a pagar de una sesión
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateAmountRequest implements Request<CalculateAmountResponse> {
    
    /**
     * ID de la sesión de parqueo para calcular el monto
     */
    private Long parkingSessionId;
}
