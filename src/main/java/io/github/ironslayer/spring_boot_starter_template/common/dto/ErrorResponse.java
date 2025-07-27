package io.github.ironslayer.spring_boot_starter_template.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Respuesta estándar para errores de la API.
 * Proporciona información detallada sobre errores para facilitar el debugging
 * y mostrar mensajes útiles al usuario final.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Código de estado HTTP
     */
    private int status;
    
    /**
     * Mensaje de error principal en inglés
     */
    private String message;
    
    /**
     * Descripción detallada del error
     */
    private String details;
    
    /**
     * Timestamp cuando ocurrió el error
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Path del endpoint donde ocurrió el error
     */
    private String path;
    
    /**
     * Tipo de error (opcional, para categorización)
     */
    private String errorType;
    
    /**
     * Constructor de conveniencia para errores simples
     */
    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor de conveniencia para errores con detalles
     */
    public ErrorResponse(int status, String message, String details, String path) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Método estático para crear el builder con timestamp automático
     */
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder().timestamp(LocalDateTime.now());
    }
}
