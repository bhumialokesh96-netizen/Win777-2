package com.win777.backend.controller;

import com.win777.backend.dto.ClaimJobResponse;
import com.win777.backend.dto.CompleteJobRequest;
import com.win777.backend.entity.SMSJob;
import com.win777.backend.service.SMSJobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for SMS task operations.
 * Handles job claiming and completion.
 */
@RestController
@RequestMapping("/api/sms-tasks")
public class SmsTaskController {

    private final SMSJobService smsJobService;

    public SmsTaskController(SMSJobService smsJobService) {
        this.smsJobService = smsJobService;
    }

    /**
     * Claims the next available SMS job for the authenticated user.
     * 
     * @param authentication the authentication object containing userId
     * @return the claimed job details
     */
    @PostMapping("/claim")
    public ResponseEntity<ClaimJobResponse> claimJob(Authentication authentication) {
        // Extract userId from JWT token
        UUID userId = (UUID) authentication.getPrincipal();

        // Claim job
        SMSJob job = smsJobService.claimSmsJob(userId);

        // Create response
        ClaimJobResponse response = new ClaimJobResponse(
            job.getId(),
            job.getRecipientNumber(),
            job.getMessageContent(),
            job.getClaimedAt()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Completes an SMS job for the authenticated user.
     * 
     * @param request the complete job request
     * @param authentication the authentication object containing userId
     * @return success message
     */
    @PostMapping("/complete")
    public ResponseEntity<String> completeJob(@Valid @RequestBody CompleteJobRequest request, 
                                              Authentication authentication) {
        // Extract userId from JWT token
        UUID userId = (UUID) authentication.getPrincipal();

        // Complete job
        smsJobService.completeSmsJob(userId, request.getJobId());

        return ResponseEntity.ok("Job completed successfully");
    }

    /**
     * Marks an SMS job as failed for the authenticated user.
     * No wallet credit is given for failed jobs.
     * 
     * @param request the complete job request (reused for jobId)
     * @param authentication the authentication object containing userId
     * @return success message
     */
    @PostMapping("/fail")
    public ResponseEntity<String> failJob(@Valid @RequestBody CompleteJobRequest request, 
                                         Authentication authentication) {
        // Extract userId from JWT token
        UUID userId = (UUID) authentication.getPrincipal();

        // Mark job as failed
        smsJobService.failSmsJob(userId, request.getJobId());

        return ResponseEntity.ok("Job marked as failed");
    }
}
