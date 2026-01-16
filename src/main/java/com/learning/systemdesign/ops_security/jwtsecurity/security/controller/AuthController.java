package com.learning.systemdesign.ops_security.jwtsecurity.security.controller;

import com.learning.systemdesign.ops_security.jwtsecurity.security.model.AuthResponse;
import com.learning.systemdesign.ops_security.jwtsecurity.security.model.LoginRequest;
import com.learning.systemdesign.ops_security.jwtsecurity.security.util.JwtUtil;
import com.learning.systemdesign.ops_security.jwtsecurity.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v7/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // 1. Check if user exists (Simple verification)
        // In real app, check password hash using BCrypt
        if (!userRepository.existsByUsername(request.username())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 2. Generate Token
        // We assume password "password" for everyone in this demo
        if ("password".equals(request.password())) {
            String token = jwtUtil.generateToken(request.username());
            return ResponseEntity.ok(new AuthResponse(token));
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
