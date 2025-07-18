package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api;


import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticatedUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.AuthenticationUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.RegisterUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserController {

    ResponseEntity<UserDTO> findById(@PathVariable Long id);

    ResponseEntity<List<UserDTO>> findAll();

    ResponseEntity<Void> update(@RequestBody UserDTO userDTO);

    ResponseEntity<Void> delete(@PathVariable Long id);

    ResponseEntity<AuthenticatedUserDTO> registerOperator(@RequestBody RegisterUserDTO request);

    ResponseEntity<AuthenticatedUserDTO> authenticate(@RequestBody AuthenticationUserDTO request);
}
