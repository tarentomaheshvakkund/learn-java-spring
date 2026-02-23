package com.learning.systemdesign.foundations.layeredarchitecture;

import com.learning.systemdesign.foundations.layeredarchitecture.user.entity.UserEntity;
import com.learning.systemdesign.foundations.layeredarchitecture.user.model.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserResponse DTO Tests")
class UserResponseTest {

    @Test
    @DisplayName("fromEntity maps entity fields to response correctly")
    void fromEntityMapsCorrectly() {
        UserEntity entity = new UserEntity("alice", "alice@example.com");
        entity.setId(42L);

        UserResponse response = UserResponse.fromEntity(entity);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("UserResponse record equality works correctly")
    void recordEquality() {
        UserResponse r1 = new UserResponse(1L, "john", "john@example.com");
        UserResponse r2 = new UserResponse(1L, "john", "john@example.com");
        UserResponse r3 = new UserResponse(2L, "jane", "jane@example.com");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
    }

    @Test
    @DisplayName("UserEntity constructor sets createdAt timestamp")
    void entityCreatedAtIsSet() {
        UserEntity entity = new UserEntity("test", "test@example.com");
        assertThat(entity.getCreatedAt()).isNotNull();
    }
}
