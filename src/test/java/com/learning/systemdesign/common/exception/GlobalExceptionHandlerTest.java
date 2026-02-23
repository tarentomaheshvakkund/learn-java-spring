package com.learning.systemdesign.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

    @Test
    @DisplayName("UserAlreadyExistsException returns 409 CONFLICT")
    void handleUserAlreadyExists() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User 'john' already exists");

        ResponseEntity<ErrorResponse> response = handler.handleUserExists(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("User 'john' already exists");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
        assertThat(response.getBody().error()).isEqualTo("Conflict");
    }

    @Test
    @DisplayName("ResourceNotFoundException returns 404 NOT FOUND")
    void handleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found with id: 99");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("User not found with id: 99");
        assertThat(response.getBody().error()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Generic Exception returns 500 INTERNAL SERVER ERROR")
    void handleGenericException() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ErrorResponse> response = handler.handleGlobal(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Something went wrong");
    }

    @Test
    @DisplayName("ErrorResponse record stores all fields correctly")
    void errorResponseRecord() {
        ErrorResponse error = new ErrorResponse(
                java.time.LocalDateTime.now(), 400, "Bad Request", "Validation failed", "/api/users"
        );

        assertThat(error.status()).isEqualTo(400);
        assertThat(error.error()).isEqualTo("Bad Request");
        assertThat(error.message()).isEqualTo("Validation failed");
        assertThat(error.path()).isEqualTo("/api/users");
        assertThat(error.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Custom exceptions extend RuntimeException")
    void customExceptionsAreRuntimeExceptions() {
        assertThat(new UserAlreadyExistsException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new ResourceNotFoundException("test")).isInstanceOf(RuntimeException.class);
    }
}
