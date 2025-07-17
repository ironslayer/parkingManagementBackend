package io.github.ironslayer.spring_boot_starter_template.it.helper;

import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticatedUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticationUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ApiHelper {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public <T> HttpEntity<T> getAuthenticatedRequest(T request, String email, String password) {
        var loginRequest = new AuthenticationUserDTO(email, password);
        var response = testRestTemplate.postForEntity(
                "/api/v1/users/authenticate",
                loginRequest,
                AuthenticatedUserDTO.class
        );

        String token = Objects.requireNonNull(response.getBody()).getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return new HttpEntity<>(request, headers);
    }
}
