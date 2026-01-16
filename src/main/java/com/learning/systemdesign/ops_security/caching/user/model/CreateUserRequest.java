package com.learning.systemdesign.ops_security.caching.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object)
 * <p>
 * SYSTEM DESIGN PRINCIPLE: Separation of Concerns.
 * We do NOT expose our database (UserEntity) directly to the outside world.
 * Instead, we use a DTO to define exactly what we accept from the API client.
 * <p>
 * We use a Java 'record' because DTOs are immutable data carriers.
 */
public record CreateUserRequest(
        
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 chars")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}
