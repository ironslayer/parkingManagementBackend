package io.github.ironslayer.spring_boot_starter_template.user.application.query.getAllUsers;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllUsersHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetAllUsersHandler handler;

    @Test
    void handle() {

        GetAllUsersRequest request = new GetAllUsersRequest();

        when(userRepository.findAll()).thenReturn(List.of(User.builder().build()));

        GetAllUsersResponse response = handler.handle(request);

        assertFalse(response.users().isEmpty());
    }

    @Test
    void getRequestType() {
        assertEquals(GetAllUsersRequest.class, handler.getRequestType());
    }
}