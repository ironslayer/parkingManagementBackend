package io.github.ironslayer.spring_boot_starter_template.config.infrastructure;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:4200"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class CorsController {

    @RequestMapping(value = "/api/**", method = RequestMethod.OPTIONS)
    public void handleCors() {
        // This method handles preflight CORS requests
    }
}
