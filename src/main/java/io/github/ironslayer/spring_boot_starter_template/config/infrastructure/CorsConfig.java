package io.github.ironslayer.spring_boot_starter_template.config.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuración centralizada de CORS para el sistema de parqueadero.
 * Esta configuración permite control granular y centralizado de las políticas CORS.
 * 
 * @author Parking Management System
 * @version 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configuración global de CORS mediante WebMvcConfigurer.
     * Esta es la forma más limpia y estándar en Spring Boot.
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000",   // React default
                    "http://localhost:5173",   // Vite default
                    "http://localhost:4200"    // Angular default
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Bean adicional para SecurityConfig.
     * Mantiene compatibilidad con la configuración de Spring Security.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",   // React default
            "http://localhost:5173",   // Vite default
            "http://localhost:4200"    // Angular default
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
