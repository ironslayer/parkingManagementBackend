package io.github.ironslayer.spring_boot_starter_template.parkingsession.infrastructure.api;

import io.github.ironslayer.spring_boot_starter_template.common.mediator.Mediator;
import io.github.ironslayer.spring_boot_starter_template.parkingsession.application.command.startSession.StartSessionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de prueba temporal para diagnosticar el problema del mediator.
 */
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {
    
    private final Mediator mediator;
    
    @GetMapping("/mediator")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OPERATOR')")
    public ResponseEntity<String> testMediator() {
        try {
            // Crear un request simple para probar
            StartSessionRequest request = new StartSessionRequest("ABC1234", 3L);
            
            // Intentar dispatch
            mediator.dispatch(request);
            
            return ResponseEntity.ok("Mediator dispatch successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
