package com.learning.systemdesign.module5.user.model;

import com.learning.systemdesign.module5.user.entity.UserEntity;

/**
 * DTO for Responses.
 * <p>
 * Controls exactly what data we send BACK to the user.
 * For example, we might have a 'password' field in Entity, but we typically NEVER create a DTO that includes it.
 */
public record UserResponse(Long id, String username, String email) {
    
    // Convenient static factory method to convert Entity -> DTO
    public static UserResponse fromEntity(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail()
        );
    }
}
