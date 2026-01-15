package com.learning.systemdesign.module2.user.service;

import com.learning.systemdesign.module2.user.model.CreateUserRequest;
import com.learning.systemdesign.module2.user.model.UserResponse;
import com.learning.systemdesign.module2.user.entity.UserEntity;
import com.learning.systemdesign.module2.user.repository.UserRepository;
import com.learning.systemdesign.module2.event.UserCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE LAYER
 * <p>
 * SYSTEM DESIGN PRINCIPLE: Encapsulation of Business Logic.
 * <p>
 * The Controller should handle HTTP (status codes, JSON).
 * The Repository should handle SQL.
 * The Service handles the actual "Thinking".
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Dependency Injection via Constructor (Best Practice)
    public UserService(UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new user with business rules.
     * Rule 1: Username must be unique.
     * Rule 2: Email must be unique.
     */
    @Transactional // Ensures atomic database operation
    public UserResponse createUser(CreateUserRequest request) {
        // Business Rule Validation
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username '" + request.username() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email '" + request.email() + "' is already in use");
        }

        // Mapping DTO -> Entity
        UserEntity newUser = new UserEntity(request.username(), request.email());

        // Saving to DB
        UserEntity savedUser = userRepository.save(newUser);

        // EVENT PUBLISHING
        // Decouple side effects!
        eventPublisher.publishEvent(new UserCreatedEvent(savedUser.getId(), savedUser.getEmail(), savedUser.getUsername()));

        // Mapping Entity -> Response DTO
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true) // Performance optimization for read-only ops
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}
