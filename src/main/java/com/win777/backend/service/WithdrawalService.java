package com.win777.backend.service;

import com.win777.backend.entity.User;
import com.win777.backend.entity.WalletLedger;
import com.win777.backend.entity.Withdrawal;
import com.win777.backend.enums.LedgerType;
import com.win777.backend.enums.WithdrawalStatus;
import com.win777.backend.repository.UserRepository;
import com.win777.backend.repository.WalletLedgerRepository;
import com.win777.backend.repository.WithdrawalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service class for withdrawal operations.
 * Handles withdrawal requests and tracking.
 */
@Service
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;
    private final WalletLedgerRepository walletLedgerRepository;

    public WithdrawalService(WithdrawalRepository withdrawalRepository,
                            UserRepository userRepository,
                            WalletLedgerRepository walletLedgerRepository) {
        this.withdrawalRepository = withdrawalRepository;
        this.userRepository = userRepository;
        this.walletLedgerRepository = walletLedgerRepository;
    }

    /**
     * Creates a new withdrawal request.
     * Validates the user has sufficient balance and creates a pending withdrawal.
     * 
     * @param userId the user ID
     * @param amount the withdrawal amount
     * @param paymentMethod the payment method
     * @param paymentDetails the payment details
     * @return the created withdrawal
     * @throws IllegalArgumentException if user not found or amount is invalid
     * @throws IllegalStateException if insufficient balance
     */
    @Transactional
    public Withdrawal createWithdrawal(UUID userId, BigDecimal amount, String paymentMethod, String paymentDetails) {
        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check balance
        BigDecimal balance = walletLedgerRepository.calculateBalance(userId);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance for withdrawal");
        }

        // Create withdrawal request
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUser(user);
        withdrawal.setAmount(amount);
        withdrawal.setPaymentMethod(paymentMethod);
        withdrawal.setPaymentDetails(paymentDetails);
        withdrawal.setStatus(WithdrawalStatus.PENDING);

        // Save withdrawal
        withdrawal = withdrawalRepository.save(withdrawal);

        // Create debit ledger entry immediately to lock funds
        // This prevents double-spending while the withdrawal is pending
        // If the withdrawal is rejected, an admin will need to create a credit entry
        WalletLedger debit = new WalletLedger();
        debit.setUser(user);
        debit.setAmount(amount.negate()); // Negative amount for debit
        debit.setLedgerType(LedgerType.WITHDRAWAL);
        debit.setDescription("Withdrawal request");
        debit.setReferenceId(withdrawal.getId());
        walletLedgerRepository.save(debit);

        return withdrawal;
    }

    /**
     * Gets all withdrawals for a user.
     * Returns paginated withdrawals ordered by creation date descending.
     * 
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return page of withdrawals
     */
    public Page<Withdrawal> getWithdrawals(UUID userId, Pageable pageable) {
        return withdrawalRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Gets withdrawals by status for a user.
     * Returns paginated withdrawals ordered by creation date descending.
     * 
     * @param userId the user ID
     * @param status the withdrawal status
     * @param pageable pagination parameters
     * @return page of withdrawals
     */
    public Page<Withdrawal> getWithdrawalsByStatus(UUID userId, WithdrawalStatus status, Pageable pageable) {
        return withdrawalRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
    }
}
