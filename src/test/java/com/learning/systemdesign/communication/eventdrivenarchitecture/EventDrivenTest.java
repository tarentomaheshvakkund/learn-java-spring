package com.learning.systemdesign.communication.eventdrivenarchitecture;

import com.learning.systemdesign.communication.eventdrivenarchitecture.event.UserCreatedEvent;
import com.learning.systemdesign.communication.eventdrivenarchitecture.user.entity.UserEntity;
import com.learning.systemdesign.communication.eventdrivenarchitecture.user.model.CreateUserRequest;
import com.learning.systemdesign.communication.eventdrivenarchitecture.user.model.UserResponse;
import com.learning.systemdesign.communication.eventdrivenarchitecture.user.repository.UserRepository;
import com.learning.systemdesign.communication.eventdrivenarchitecture.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Event-Driven Architecture Tests")
class EventDrivenTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("createUser publishes UserCreatedEvent after saving")
    void createUserPublishesEvent() {
        CreateUserRequest request = new CreateUserRequest("john", "john@example.com");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        userService.createUser(request);

        ArgumentCaptor<UserCreatedEvent> eventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        UserCreatedEvent event = eventCaptor.getValue();
        assertThat(event.userId()).isEqualTo(1L);
        assertThat(event.email()).isEqualTo("john@example.com");
        assertThat(event.username()).isEqualTo("john");
    }

    @Test
    @DisplayName("createUser does not publish event when username is duplicate")
    void createUserDoesNotPublishEventOnDuplicate() {
        CreateUserRequest request = new CreateUserRequest("john", "john@example.com");
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("UserCreatedEvent record stores data correctly")
    void userCreatedEventRecord() {
        UserCreatedEvent event = new UserCreatedEvent(1L, "test@example.com", "testuser");

        assertThat(event.userId()).isEqualTo(1L);
        assertThat(event.email()).isEqualTo("test@example.com");
        assertThat(event.username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("createUser returns correct response DTO")
    void createUserReturnsCorrectResponse() {
        CreateUserRequest request = new CreateUserRequest("alice", "alice@example.com");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(5L);
            return entity;
        });

        UserResponse response = userService.createUser(request);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
    }
}
