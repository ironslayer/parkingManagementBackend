package io.github.ironslayer.spring_boot_starter_template.rateconfig.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA para configuraciones de tarifas
 * 
 * Mapea la tabla rate_configs de la base de datos según el esquema definido:
 * - id: clave primaria autoincremental
 * - vehicle_type_id: referencia al tipo de vehículo
 * - rate_per_hour: tarifa por hora con precisión decimal
 * - minimum_charge_hours: mínimo de horas a cobrar
 * - maximum_daily_rate: tarifa máxima diaria opcional
 * - is_active: indica si la configuración está activa
 * - created_at: fecha de creación automática
 * - updated_at: fecha de última actualización automática
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rate_configs")
public class RateConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vehicle_type_id", nullable = false)
    private Long vehicleTypeId;
    
    @Column(name = "rate_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerHour;
    
    @Builder.Default
    @Column(name = "minimum_charge_hours", nullable = false)
    private Integer minimumChargeHours = 1;
    
    @Column(name = "maximum_daily_rate", precision = 10, scale = 2)
    private BigDecimal maximumDailyRate;
    
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
