package io.github.ironslayer.spring_boot_starter_template.user.application.command.deleteUser;

import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserHandler handler;

    @Test
    void handle() {

        DeleteUserRequest request = new DeleteUserRequest(1L);

        handler.handle(request);

        verify(userRepository).deleteById(request.userId());
    }

    @Test
    void getRequestType() {
        assertEquals(DeleteUserRequest.class, handler.getRequestType());
    }
}