package com.win777.backend.dto;

import com.win777.backend.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for withdrawal response.
 */
public class WithdrawalResponse {

    private UUID id;
    private BigDecimal amount;
    private WithdrawalStatus status;
    private String paymentMethod;
    private String paymentDetails;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public WithdrawalResponse() {
    }

    public WithdrawalResponse(UUID id, BigDecimal amount, WithdrawalStatus status, String paymentMethod, 
                             String paymentDetails, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentDetails = paymentDetails;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
