package com.win777.backend.controller;

import com.win777.backend.dto.WithdrawalRequest;
import com.win777.backend.dto.WithdrawalResponse;
import com.win777.backend.entity.Withdrawal;
import com.win777.backend.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for withdrawal operations.
 * Handles withdrawal requests and tracking.
 */
@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    /**
     * Creates a new withdrawal request for the authenticated user.
     * 
     * @param request the withdrawal request
     * @param authentication the authentication object containing userId
     * @return the created withdrawal
     */
    @PostMapping
    public ResponseEntity<WithdrawalResponse> createWithdrawal(
            @Valid @RequestBody WithdrawalRequest request,
            Authentication authentication) {
        
        // Extract userId from JWT token
        UUID userId = (UUID) authentication.getPrincipal();

        // Create withdrawal
        Withdrawal withdrawal = withdrawalService.createWithdrawal(
            userId,
            request.getAmount(),
            request.getPaymentMethod(),
            request.getPaymentDetails()
        );

        // Create response
        WithdrawalResponse response = new WithdrawalResponse(
            withdrawal.getId(),
            withdrawal.getAmount(),
            withdrawal.getStatus(),
            withdrawal.getPaymentMethod(),
            withdrawal.getPaymentDetails(),
            withdrawal.getCreatedAt(),
            withdrawal.getProcessedAt()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Gets all withdrawals for the authenticated user.
     * 
     * @param authentication the authentication object containing userId
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return page of withdrawals
     */
    @GetMapping
    public ResponseEntity<Page<WithdrawalResponse>> getWithdrawals(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Extract userId from JWT token
        UUID userId = (UUID) authentication.getPrincipal();

        // Get withdrawals
        Pageable pageable = PageRequest.of(page, size);
        Page<Withdrawal> withdrawalPage = withdrawalService.getWithdrawals(userId, pageable);

        // Convert to DTOs
        Page<WithdrawalResponse> responsePage = withdrawalPage.map(withdrawal -> new WithdrawalResponse(
            withdrawal.getId(),
            withdrawal.getAmount(),
            withdrawal.getStatus(),
            withdrawal.getPaymentMethod(),
            withdrawal.getPaymentDetails(),
            withdrawal.getCreatedAt(),
            withdrawal.getProcessedAt()
        ));

        return ResponseEntity.ok(responsePage);
    }
}
