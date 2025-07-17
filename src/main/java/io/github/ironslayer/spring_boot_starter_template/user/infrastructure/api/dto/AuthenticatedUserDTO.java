package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUserDTO {

    private String token;
}
