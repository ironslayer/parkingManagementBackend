package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.repository;

import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserCacheEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CacheQueryUserRepository extends CrudRepository<UserCacheEntity, Long> {

    Boolean existsByEmail(String email);

    Optional<UserCacheEntity> findByEmail(String email);
}
