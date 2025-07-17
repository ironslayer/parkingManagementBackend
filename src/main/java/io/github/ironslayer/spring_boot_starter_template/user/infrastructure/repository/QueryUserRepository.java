package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.repository;


import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueryUserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}
