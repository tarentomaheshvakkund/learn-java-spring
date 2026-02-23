package com.learning.systemdesign.ops_security.jwtsecurity;

import com.learning.systemdesign.ops_security.jwtsecurity.security.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JWT Utility Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    @DisplayName("generateToken creates a non-null token")
    void generateTokenCreatesToken() {
        String token = jwtUtil.generateToken("testuser");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("extractUsername returns correct username from token")
    void extractUsernameFromToken() {
        String token = jwtUtil.generateToken("alice");
        String username = jwtUtil.extractUsername(token);
        assertThat(username).isEqualTo("alice");
    }

    @Test
    @DisplayName("validateToken returns true for valid token and matching username")
    void validateTokenSuccess() {
        String token = jwtUtil.generateToken("bob");
        Boolean isValid = jwtUtil.validateToken(token, "bob");
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateToken returns false for mismatched username")
    void validateTokenMismatchedUsername() {
        String token = jwtUtil.generateToken("alice");
        Boolean isValid = jwtUtil.validateToken(token, "bob");
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Different users get different tokens")
    void differentUsersGetDifferentTokens() {
        String token1 = jwtUtil.generateToken("alice");
        String token2 = jwtUtil.generateToken("bob");
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Token has three parts (header.payload.signature)")
    void tokenHasThreeParts() {
        String token = jwtUtil.generateToken("testuser");
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    @DisplayName("Invalid token throws exception")
    void invalidTokenThrowsException() {
        assertThatThrownBy(() -> jwtUtil.extractUsername("invalid.token.here"))
                .isInstanceOf(Exception.class);
    }
}
