package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.repository.implementation;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserCacheEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper.UserMapper;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.repository.CacheQueryUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisUserRepositoryImplTest {

    @Mock
    private CacheQueryUserRepository cacheQueryUserRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RedisUserRepositoryImpl redisUserRepository;

    private final UserCacheEntity userCacheEntity = UserCacheEntity.builder().id(1L).build();
    private final User user = User.builder().id(1L).build();

    @Test
    void save() {
        when(userMapper.userToUserCacheEntity(any(User.class))).thenReturn(userCacheEntity);
        when(userMapper.userCacheEntityToUser(any(UserCacheEntity.class))).thenReturn(user);
        when(cacheQueryUserRepository.save(userCacheEntity)).thenReturn(userCacheEntity);

        redisUserRepository.save(user);

        verify(cacheQueryUserRepository).save(any(UserCacheEntity.class));
    }

    @Test
    void findById() {
        when(userMapper.userCacheEntityToUser(any(UserCacheEntity.class))).thenReturn(user);
        when(cacheQueryUserRepository.findById(1L)).thenReturn(Optional.of(userCacheEntity));

        Optional<User> optionalUser = redisUserRepository.findById(1L);

        assertTrue(optionalUser.isPresent());
    }

    @Test
    void findAll() {
        when(userMapper.userCacheEntityToUser(any(UserCacheEntity.class))).thenReturn(user);
        when(cacheQueryUserRepository.findAll()).thenReturn(List.of(userCacheEntity));

        List<User> users = redisUserRepository.findAll();
        assertFalse(users.isEmpty());
    }

    @Test
    void findByEmail() {
        when(userMapper.userCacheEntityToUser(any(UserCacheEntity.class))).thenReturn(user);
        when(cacheQueryUserRepository.findByEmail(anyString())).thenReturn(Optional.of(userCacheEntity));

        Optional<User> user = redisUserRepository.findByEmail("test@gmail.com");
        assertTrue(user.isPresent());
    }

    @Test
    void existsByEmail() {
        when(cacheQueryUserRepository.existsByEmail(anyString())).thenReturn(true);

        Boolean existsByEmail = redisUserRepository.existsByEmail("test@gmail.com");
        assertTrue(existsByEmail);
    }

    @Test
    void deleteById() {

        redisUserRepository.deleteById(1L);
        verify(cacheQueryUserRepository).deleteById(1L);
    }
}