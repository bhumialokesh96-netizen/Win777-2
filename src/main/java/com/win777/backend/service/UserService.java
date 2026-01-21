package com.win777.backend.service;

import com.win777.backend.entity.User;
import com.win777.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service class for user operations.
 * Handles user registration and authentication.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * 
     * @param username the username
     * @param email the email
     * @param password the password
     * @param phoneNumber the phone number
     * @param referralCode the referral code (optional)
     * @return the created user
     * @throws IllegalArgumentException if username, email, or phone number already exists
     */
    @Transactional
    public User registerUser(String username, String email, String password, String phoneNumber, String referralCode) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user.setReferralCode(generateReferralCode());

        // Set referrer if referral code provided
        if (referralCode != null && !referralCode.isEmpty()) {
            Optional<User> referrer = userRepository.findByReferralCode(referralCode);
            if (referrer.isPresent()) {
                user.setReferrer(referrer.get());
            } else {
                throw new IllegalArgumentException("Invalid referral code");
            }
        }

        return userRepository.save(user);
    }

    /**
     * Authenticates a user by username and password.
     * 
     * @param username the username
     * @param password the password
     * @return the authenticated user
     * @throws IllegalArgumentException if username or password is invalid
     */
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return user;
    }

    /**
     * Finds a user by ID.
     * 
     * @param userId the user ID
     * @return the user
     * @throws IllegalArgumentException if user not found
     */
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return optional containing the user if found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Generates a unique referral code for a user.
     * 
     * @return a unique referral code
     */
    private String generateReferralCode() {
        String code;
        do {
            code = "REF" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (userRepository.findByReferralCode(code).isPresent());
        return code;
    }
}
