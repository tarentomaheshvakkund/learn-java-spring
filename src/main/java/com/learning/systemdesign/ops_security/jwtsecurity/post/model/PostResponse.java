package com.learning.systemdesign.ops_security.jwtsecurity.post.model;

import com.learning.systemdesign.ops_security.jwtsecurity.post.entity.PostEntity;

import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    String title,
    String body,
    LocalDateTime createdAt
) {
    public static PostResponse fromEntity(PostEntity entity) {
        return new PostResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getBody(),
            entity.getCreatedAt()
        );
    }
}
