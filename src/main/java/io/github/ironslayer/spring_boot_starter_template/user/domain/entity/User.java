package io.github.ironslayer.spring_boot_starter_template.user.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
}
