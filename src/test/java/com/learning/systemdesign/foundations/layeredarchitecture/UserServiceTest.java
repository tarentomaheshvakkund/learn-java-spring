package com.learning.systemdesign.foundations.layeredarchitecture;

import com.learning.systemdesign.foundations.layeredarchitecture.user.entity.UserEntity;
import com.learning.systemdesign.foundations.layeredarchitecture.user.model.CreateUserRequest;
import com.learning.systemdesign.foundations.layeredarchitecture.user.model.UserResponse;
import com.learning.systemdesign.foundations.layeredarchitecture.user.repository.UserRepository;
import com.learning.systemdesign.foundations.layeredarchitecture.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Layered Architecture - UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("createUser succeeds with valid unique user")
    void createUserSuccess() {
        CreateUserRequest request = new CreateUserRequest("john", "john@example.com");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        UserResponse response = userService.createUser(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.email()).isEqualTo("john@example.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("createUser throws when username already exists")
    void createUserDuplicateUsername() {
        CreateUserRequest request = new CreateUserRequest("john", "john@example.com");
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser throws when email already exists")
    void createUserDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest("john", "john@example.com");
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllUsers returns empty list when no users exist")
    void getAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> users = userService.getAllUsers();

        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("getAllUsers returns all users mapped to DTOs")
    void getAllUsersReturnsAll() {
        UserEntity user1 = new UserEntity("alice", "alice@example.com");
        user1.setId(1L);
        UserEntity user2 = new UserEntity("bob", "bob@example.com");
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponse> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).username()).isEqualTo("alice");
        assertThat(users.get(1).username()).isEqualTo("bob");
    }
}
