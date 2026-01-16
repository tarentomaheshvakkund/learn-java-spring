package com.learning.systemdesign.ops_security.caching.user.service;

import com.learning.systemdesign.ops_security.caching.user.model.CreateUserRequest;
import com.learning.systemdesign.ops_security.caching.user.model.UserResponse;
import com.learning.systemdesign.ops_security.caching.user.entity.UserEntity;
import com.learning.systemdesign.ops_security.caching.user.repository.UserRepository;
import com.learning.systemdesign.common.exception.UserAlreadyExistsException;
import org.springframework.cache.annotation.Cacheable;
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

    // Dependency Injection via Constructor (Best Practice)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            throw new UserAlreadyExistsException("Username '" + request.username() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email '" + request.email() + "' is already in use");
        }

        // Mapping DTO -> Entity
        UserEntity newUser = new UserEntity(request.username(), request.email());

        // Saving to DB
        UserEntity savedUser = userRepository.save(newUser);

        // Mapping Entity -> Response DTO
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long id) {
        System.out.println("Fetching User from Database for ID: " + id); // Simulation of expensive DB call
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("User not found: " + id)); // In real app, use ResourceNotFoundContext
    }

    @Transactional(readOnly = true) // Performance optimization for read-only ops
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}
