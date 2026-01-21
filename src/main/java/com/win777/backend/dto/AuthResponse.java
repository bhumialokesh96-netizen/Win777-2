package com.win777.backend.dto;

import java.util.UUID;

/**
 * DTO for authentication response.
 */
public class AuthResponse {

    private String token;
    private UUID userId;
    private String username;
    private String email;
    private String referralCode;

    public AuthResponse() {
    }

    public AuthResponse(String token, UUID userId, String username, String email, String referralCode) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.referralCode = referralCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
}
