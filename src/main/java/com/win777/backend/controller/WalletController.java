package com.win777.backend.controller;

import com.win777.backend.dto.TransactionDto;
import com.win777.backend.dto.WalletBalanceResponse;
import com.win777.backend.entity.WalletLedger;
import com.win777.backend.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Controller for wallet operations.
 * Provides read-only access to wallet balance and transaction history.
 */
@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Gets the wallet balance for the authenticated user.
     * 
     * @param authentication the authentication object containing userId
     * @return the wallet balance
     */
    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceResponse> getBalance(Authentication authentication) {
        // Extract userId from JWT token
        UUID userId = (UUID) authentication.getPrincipal();

        // Get balance
        BigDecimal balance = walletService.getBalance(userId);

        return ResponseEntity.ok(new WalletBalanceResponse(balance));
    }

    /**
     * Gets the transaction history for the authenticated user.
     * 
     * @param authentication the authentication object containing userId
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return page of transaction history
     */
    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionDto>> getTransactionHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Extract userId from JWT token
        UUID userId = (UUID) authentication.getPrincipal();

        // Get transaction history
        Pageable pageable = PageRequest.of(page, size);
        Page<WalletLedger> ledgerPage = walletService.getTransactionHistory(userId, pageable);

        // Convert to DTOs
        Page<TransactionDto> transactionPage = ledgerPage.map(ledger -> new TransactionDto(
            ledger.getId(),
            ledger.getAmount(),
            ledger.getLedgerType(),
            ledger.getDescription(),
            ledger.getReferenceId(),
            ledger.getCreatedAt()
        ));

        return ResponseEntity.ok(transactionPage);
    }
}
