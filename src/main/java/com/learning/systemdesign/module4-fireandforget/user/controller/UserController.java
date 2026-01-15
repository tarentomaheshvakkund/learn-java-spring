package com.learning.systemdesign.module4.user.controller;

import com.learning.systemdesign.module4.user.model.CreateUserRequest;
import com.learning.systemdesign.module4.user.model.UserResponse;
import com.learning.systemdesign.module4.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API LAYER (Controller)
 * <p>
 * SYSTEM DESIGN PRINCIPLE: Interface Protocol.
 * <p>
 * This layer talks "HTTP" (JSON, Status Codes).
 * It delegates all "Logic" to the Service Layer.
 * <p>
 * DESIGN PATTERN:
 * We use Constructor Injection for the Service.
 * We return ResponseEntity<DTO> to control headers and body safely.
 */
@RestController
@RequestMapping("/api/v4/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/users
     * Creates a new user.
     * <p>
     * @Valid triggers Bean Validation on the DTO (e.g. @NotBlank).
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * GET /api/users
     * Returns a list of all users.
     */
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}
