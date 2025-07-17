package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto;


import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.annotation.MaskData;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String firstname;
    private String lastname;
    @MaskData
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;

}
