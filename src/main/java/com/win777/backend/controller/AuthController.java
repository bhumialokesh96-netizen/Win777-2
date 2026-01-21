package com.win777.backend.controller;

import com.win777.backend.dto.AuthResponse;
import com.win777.backend.dto.LoginRequest;
import com.win777.backend.dto.RegisterRequest;
import com.win777.backend.entity.User;
import com.win777.backend.security.JwtUtil;
import com.win777.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authentication.
 * Handles registration and login endpoints.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user.
     * 
     * @param request the registration request
     * @return the authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Register user
        User user = userService.registerUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getPhoneNumber(),
            request.getReferralCode()
        );

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // Create response
        AuthResponse response = new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getReferralCode()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user.
     * 
     * @param request the login request
     * @return the authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Authenticate user
        User user = userService.authenticateUser(request.getUsername(), request.getPassword());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // Create response
        AuthResponse response = new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getReferralCode()
        );

        return ResponseEntity.ok(response);
    }
}
