package com.win777.backend.service;

import com.win777.backend.entity.WalletLedger;
import com.win777.backend.repository.WalletLedgerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service class for wallet operations.
 * Provides read-only access to wallet balance and transaction history.
 */
@Service
public class WalletService {

    private final WalletLedgerRepository walletLedgerRepository;

    public WalletService(WalletLedgerRepository walletLedgerRepository) {
        this.walletLedgerRepository = walletLedgerRepository;
    }

    /**
     * Gets the wallet balance for a user.
     * Calculates the balance by summing all ledger entries.
     * 
     * @param userId the user ID
     * @return the wallet balance
     */
    public BigDecimal getBalance(UUID userId) {
        return walletLedgerRepository.calculateBalance(userId);
    }

    /**
     * Gets the transaction history for a user.
     * Returns paginated ledger entries ordered by creation date descending.
     * 
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return page of wallet ledger entries
     */
    public Page<WalletLedger> getTransactionHistory(UUID userId, Pageable pageable) {
        return walletLedgerRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
