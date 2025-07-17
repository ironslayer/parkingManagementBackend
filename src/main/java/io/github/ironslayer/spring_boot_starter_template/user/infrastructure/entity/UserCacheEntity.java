package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("User")
@Data
@Builder
public class UserCacheEntity implements Serializable {

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
}



