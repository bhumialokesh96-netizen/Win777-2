package com.win777.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for complete job request.
 */
public class CompleteJobRequest {

    @NotNull(message = "Job ID is required")
    private UUID jobId;

    public CompleteJobRequest() {
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }
}
