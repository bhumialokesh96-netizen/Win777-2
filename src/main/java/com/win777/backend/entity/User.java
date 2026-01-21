package com.win777.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    // Daily SMS tracking
    @Column(name = "daily_sms_limit", nullable = false)
    private Integer dailySmsLimit = 100;

    @Column(name = "daily_sms_sent_count", nullable = false)
    private Integer dailySmsSentCount = 0;

    @Column(name = "last_sms_reset_date")
    private LocalDate lastSmsResetDate;

    // Referral tree support
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id")
    private User referrer;

    @Column(name = "referral_code", unique = true, nullable = false)
    private String referralCode;

    // Auditing fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public User() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getDailySmsLimit() {
        return dailySmsLimit;
    }

    public void setDailySmsLimit(Integer dailySmsLimit) {
        this.dailySmsLimit = dailySmsLimit;
    }

    public Integer getDailySmsSentCount() {
        return dailySmsSentCount;
    }

    public void setDailySmsSentCount(Integer dailySmsSentCount) {
        this.dailySmsSentCount = dailySmsSentCount;
    }

    public LocalDate getLastSmsResetDate() {
        return lastSmsResetDate;
    }

    public void setLastSmsResetDate(LocalDate lastSmsResetDate) {
        this.lastSmsResetDate = lastSmsResetDate;
    }

    public User getReferrer() {
        return referrer;
    }

    public void setReferrer(User referrer) {
        this.referrer = referrer;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
