package com.win777.backend.dto;

import com.win777.backend.enums.LedgerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for transaction history item.
 */
public class TransactionDto {

    private UUID id;
    private BigDecimal amount;
    private LedgerType ledgerType;
    private String description;
    private UUID referenceId;
    private LocalDateTime createdAt;

    public TransactionDto() {
    }

    public TransactionDto(UUID id, BigDecimal amount, LedgerType ledgerType, String description, UUID referenceId, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.ledgerType = ledgerType;
        this.description = description;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
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

    public LedgerType getLedgerType() {
        return ledgerType;
    }

    public void setLedgerType(LedgerType ledgerType) {
        this.ledgerType = ledgerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
