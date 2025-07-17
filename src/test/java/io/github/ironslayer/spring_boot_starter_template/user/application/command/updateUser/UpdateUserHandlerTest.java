package io.github.ironslayer.spring_boot_starter_template.user.application.command.updateUser;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserHandler handler;

    @Test
    void handle() {

        User user = User.builder().id(1L).build();

        UpdateUserRequest request = new UpdateUserRequest(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        handler.handle(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void getRequestType() {
        assertEquals(UpdateUserRequest.class, handler.getRequestType());
    }
}