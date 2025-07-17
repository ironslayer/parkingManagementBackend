package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.repository.implementation;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserCacheEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.repository.CacheQueryUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Repository
public class RedisUserRepositoryImpl implements UserRepository {

    private final CacheQueryUserRepository cacheQueryUserRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User product) {
        UserCacheEntity saved = cacheQueryUserRepository.save(userMapper.userToUserCacheEntity(product));
        return userMapper.userCacheEntityToUser(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return cacheQueryUserRepository.findById(id).map(userMapper::userCacheEntityToUser);
    }

    @Override
    public List<User> findAll() {
        Iterable<UserCacheEntity> all = cacheQueryUserRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false).map(userMapper::userCacheEntityToUser).toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return cacheQueryUserRepository.findByEmail(email).map(userMapper::userCacheEntityToUser);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return cacheQueryUserRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        cacheQueryUserRepository.deleteById(id);
    }
}
