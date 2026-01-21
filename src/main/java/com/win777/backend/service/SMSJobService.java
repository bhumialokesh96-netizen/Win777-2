package com.win777.backend.service;

import com.win777.backend.entity.SMSJob;
import com.win777.backend.entity.SMSRateConfig;
import com.win777.backend.entity.User;
import com.win777.backend.entity.WalletLedger;
import com.win777.backend.enums.LedgerType;
import com.win777.backend.enums.SMSJobStatus;
import com.win777.backend.repository.SMSJobRepository;
import com.win777.backend.repository.SMSRateConfigRepository;
import com.win777.backend.repository.UserRepository;
import com.win777.backend.repository.WalletLedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for managing SMS job operations.
 * Handles job completion, earnings calculation, and referral bonuses.
 */
@Service
public class SMSJobService {

    private final SMSJobRepository smsJobRepository;
    private final UserRepository userRepository;
    private final WalletLedgerRepository walletLedgerRepository;
    private final SMSRateConfigRepository smsRateConfigRepository;

    public SMSJobService(
            SMSJobRepository smsJobRepository,
            UserRepository userRepository,
            WalletLedgerRepository walletLedgerRepository,
            SMSRateConfigRepository smsRateConfigRepository) {
        this.smsJobRepository = smsJobRepository;
        this.userRepository = userRepository;
        this.walletLedgerRepository = walletLedgerRepository;
        this.smsRateConfigRepository = smsRateConfigRepository;
    }

    /**
     * Completes an SMS job, validates daily limits, awards earnings, and distributes referral bonuses.
     * 
     * @param jobId the ID of the job to complete
     * @throws IllegalStateException if job not found, not claimed, daily limit reached, or no active rate config
     */
    @Transactional
    public void completeSmsJob(UUID jobId) {
        // Fetch job with pessimistic lock
        SMSJob job = smsJobRepository.findByIdWithLock(jobId)
                .orElseThrow(() -> new IllegalStateException("SMS job not found: " + jobId));

        // Validate job is in CLAIMED status
        if (job.getStatus() != SMSJobStatus.CLAIMED) {
            throw new IllegalStateException("SMS job is not in CLAIMED status: " + jobId);
        }

        // Fetch user with pessimistic lock
        User user = userRepository.findByIdWithLock(job.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("User not found: " + job.getUser().getId()));

        // Validate daily SMS limit
        if (user.getDailySmsSentCount() >= user.getDailySmsLimit()) {
            throw new IllegalStateException(
                    "Daily SMS limit reached for user: " + user.getId() 
                    + " (sent: " + user.getDailySmsSentCount() 
                    + ", limit: " + user.getDailySmsLimit() + ")");
        }

        // Fetch active SMS rate configuration
        SMSRateConfig rateConfig = smsRateConfigRepository.findByIsActive(true)
                .orElseThrow(() -> new IllegalStateException("No active SMS rate configuration found"));

        // Create earnings ledger entry
        WalletLedger earnings = new WalletLedger();
        earnings.setUser(user);
        earnings.setAmount(rateConfig.getSmsEarningRate());
        earnings.setLedgerType(LedgerType.EARNINGS);
        walletLedgerRepository.save(earnings);

        // Distribute referral bonuses (3 levels)
        distributeReferralBonuses(user, rateConfig.getSmsEarningRate());

        // Increment daily SMS sent count
        user.setDailySmsSentCount(user.getDailySmsSentCount() + 1);
        userRepository.save(user);

        // Mark job as completed
        job.setStatus(SMSJobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        smsJobRepository.save(job);
    }

    /**
     * Distributes referral bonuses to up to 3 levels of referrers.
     * 
     * @param user the user who completed the SMS job
     * @param baseEarnings the base earnings amount
     */
    private void distributeReferralBonuses(User user, BigDecimal baseEarnings) {
        // Level 1 referrer (10% of base earnings)
        User level1Referrer = user.getReferrer();
        if (level1Referrer != null) {
            BigDecimal level1Bonus = baseEarnings.multiply(new BigDecimal("0.10"));
            createReferralBonus(level1Referrer, level1Bonus, LedgerType.REFERRAL_LEVEL_1);

            // Level 2 referrer (5% of base earnings)
            User level2Referrer = level1Referrer.getReferrer();
            if (level2Referrer != null) {
                BigDecimal level2Bonus = baseEarnings.multiply(new BigDecimal("0.05"));
                createReferralBonus(level2Referrer, level2Bonus, LedgerType.REFERRAL_LEVEL_2);

                // Level 3 referrer (2% of base earnings)
                User level3Referrer = level2Referrer.getReferrer();
                if (level3Referrer != null) {
                    BigDecimal level3Bonus = baseEarnings.multiply(new BigDecimal("0.02"));
                    createReferralBonus(level3Referrer, level3Bonus, LedgerType.REFERRAL_LEVEL_3);
                }
            }
        }
    }

    /**
     * Creates a referral bonus ledger entry.
     * 
     * @param referrer the user receiving the bonus
     * @param amount the bonus amount
     * @param ledgerType the ledger type for the bonus level
     */
    private void createReferralBonus(User referrer, BigDecimal amount, LedgerType ledgerType) {
        WalletLedger bonus = new WalletLedger();
        bonus.setUser(referrer);
        bonus.setAmount(amount);
        bonus.setLedgerType(ledgerType);
        walletLedgerRepository.save(bonus);
    }
}
