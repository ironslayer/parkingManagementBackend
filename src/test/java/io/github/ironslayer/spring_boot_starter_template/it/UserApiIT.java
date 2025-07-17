package io.github.ironslayer.spring_boot_starter_template.it;

import io.github.ironslayer.spring_boot_starter_template.it.helper.ApiHelper;
import io.github.ironslayer.spring_boot_starter_template.it.helper.Constants;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticatedUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticationUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.RegisterUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserApiIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ApiHelper apiHelper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(scripts = "/it/data-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void registerUser() {
        RegisterUserDTO request = RegisterUserDTO.builder()
                .email("john@example.com")
                .password("12345")
                .firstname("John")
                .lastname("Doe")
                .build();

        ResponseEntity<AuthenticatedUserDTO> response = testRestTemplate.postForEntity(
                "/api/v1/users/register",
                request,
                AuthenticatedUserDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    @Sql(scripts = "/it/user/authenticate/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/it/data-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void authenticateUser() {
        AuthenticationUserDTO request = AuthenticationUserDTO.builder()
                .email("test@gmail.com")
                .password("password")
                .build();

        ResponseEntity<AuthenticatedUserDTO> response = testRestTemplate.postForEntity(
                "/api/v1/users/authenticate",
                request,
                AuthenticatedUserDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    @Sql(scripts = "/it/user/findById/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/it/data-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById() {

        HttpEntity<RegisterUserDTO> entity = apiHelper.getAuthenticatedRequest(null, Constants.DEFAULT_USER, Constants.DEFAULT_PASSWORD);

        ResponseEntity<UserDTO> response = testRestTemplate.exchange(
                "/api/v1/users/1",
                HttpMethod.GET,
                entity,
                UserDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    @Test
    @Sql(scripts = "/it/user/findAll/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/it/data-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByAll() {

        HttpEntity<RegisterUserDTO> entity = apiHelper.getAuthenticatedRequest(null, Constants.DEFAULT_USER, Constants.DEFAULT_PASSWORD);

        ResponseEntity<List<UserDTO>> response = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getFirst());
    }

    @Test
    @Sql(scripts = "/it/user/update/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/it/data-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update() {

        UserDTO request = UserDTO.builder()
                .id(1L)
                .firstname("Alvaro")
                .lastname("Guarachi")
                .build();

        HttpEntity<UserDTO> entity = apiHelper.getAuthenticatedRequest(request, Constants.DEFAULT_USER, Constants.DEFAULT_PASSWORD);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        User user = userRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertEquals("Alvaro", user.getFirstname());
    }

    @Test
    @Sql(scripts = "/it/user/delete/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/it/data-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete() {

        HttpEntity<UserDTO> entity = apiHelper.getAuthenticatedRequest(null, Constants.DEFAULT_USER, Constants.DEFAULT_PASSWORD);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/users/1",
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Optional<User> user = userRepository.findById(1L);

        assertTrue(user.isEmpty());
    }

}
