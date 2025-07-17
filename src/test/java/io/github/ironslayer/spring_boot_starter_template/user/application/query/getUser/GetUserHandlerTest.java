package io.github.ironslayer.spring_boot_starter_template.user.application.query.getUser;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserHandler handler;

    @Test
    void handle() {

        GetUserRequest request = new GetUserRequest(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));

        GetUserResponse response = handler.handle(request);
        assertEquals(1L, response.user().getId());
    }

    @Test
    void getRequestType() {
        assertEquals(GetUserRequest.class, handler.getRequestType());
    }
}