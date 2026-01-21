package com.win777.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sms_rate_config")
public class SMSRateConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // BigDecimal for monetary rate
    @Column(name = "sms_earning_rate", nullable = false, precision = 19, scale = 2)
    private BigDecimal smsEarningRate;

    // Backend-controlled flag for single active row
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "minimum_payout", precision = 19, scale = 2)
    private BigDecimal minimumPayout;

    @Column(name = "maximum_daily_earnings", precision = 19, scale = 2)
    private BigDecimal maximumDailyEarnings;

    // Auditing fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SMSRateConfig() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getSmsEarningRate() {
        return smsEarningRate;
    }

    public void setSmsEarningRate(BigDecimal smsEarningRate) {
        this.smsEarningRate = smsEarningRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMinimumPayout() {
        return minimumPayout;
    }

    public void setMinimumPayout(BigDecimal minimumPayout) {
        this.minimumPayout = minimumPayout;
    }

    public BigDecimal getMaximumDailyEarnings() {
        return maximumDailyEarnings;
    }

    public void setMaximumDailyEarnings(BigDecimal maximumDailyEarnings) {
        this.maximumDailyEarnings = maximumDailyEarnings;
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
