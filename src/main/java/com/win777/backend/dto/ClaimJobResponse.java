package com.win777.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for claim job response.
 */
public class ClaimJobResponse {

    private UUID jobId;
    private String recipientNumber;
    private String messageContent;
    private LocalDateTime claimedAt;

    public ClaimJobResponse() {
    }

    public ClaimJobResponse(UUID jobId, String recipientNumber, String messageContent, LocalDateTime claimedAt) {
        this.jobId = jobId;
        this.recipientNumber = recipientNumber;
        this.messageContent = messageContent;
        this.claimedAt = claimedAt;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }
}
