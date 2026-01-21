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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class for SMS job operations.
 * Handles transactional logic for completing SMS jobs and distributing rewards.
 */
@Service
public class SMSJobService {

    private final SMSJobRepository smsJobRepository;
    private final UserRepository userRepository;
    private final WalletLedgerRepository walletLedgerRepository;
    private final SMSRateConfigRepository smsRateConfigRepository;

    // Referral reward percentages
    private static final BigDecimal LEVEL_1_PERCENTAGE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal LEVEL_2_PERCENTAGE = new BigDecimal("0.02"); // 2%
    private static final BigDecimal LEVEL_3_PERCENTAGE = new BigDecimal("0.01"); // 1%

    public SMSJobService(SMSJobRepository smsJobRepository,
                         UserRepository userRepository,
                         WalletLedgerRepository walletLedgerRepository,
                         SMSRateConfigRepository smsRateConfigRepository) {
        this.smsJobRepository = smsJobRepository;
        this.userRepository = userRepository;
        this.walletLedgerRepository = walletLedgerRepository;
        this.smsRateConfigRepository = smsRateConfigRepository;
    }

    /**
     * Completes an SMS job with transactional integrity.
     * Validates ownership, updates job status, credits earnings, and distributes referral rewards.
     * Enforces daily SMS limits.
     * 
     * @param userId the ID of the user completing the job
     * @param jobId the ID of the job to complete
     * @throws IllegalArgumentException if user or job not found
     * @throws IllegalStateException if ownership validation fails, job is not in CLAIMED status, 
     *                               daily SMS limit reached, or no active SMS rate configuration is found
     */
    @Transactional
    public void completeSmsJob(UUID userId, UUID jobId) {
        // 1. Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. Check daily SMS limit before processing
        if (user.getDailySmsSentCount() >= user.getDailySmsLimit()) {
            throw new IllegalStateException("Daily SMS limit reached");
        }

        // 3. Fetch SMS job with pessimistic lock for concurrent safety
        SMSJob smsJob = smsJobRepository.findByIdAndUserIdWithLock(jobId, userId)
                .orElseThrow(() -> new IllegalStateException("SMS job not found or user does not own this job"));

        // 4. Validate job status - must be CLAIMED
        if (smsJob.getStatus() != SMSJobStatus.CLAIMED) {
            throw new IllegalStateException("SMS job must be in CLAIMED status to be completed. Current status: " + smsJob.getStatus());
        }

        // 5. Get active SMS rate configuration
        SMSRateConfig activeConfig = smsRateConfigRepository.findByIsActive(true)
                .orElseThrow(() -> new IllegalStateException("No active SMS rate configuration found"));

        BigDecimal smsEarningRate = activeConfig.getSmsEarningRate();

        // 6. Increment daily SMS count
        user.setDailySmsSentCount(user.getDailySmsSentCount() + 1);
        userRepository.save(user);

        // 7. Update SMS job status to COMPLETED
        smsJob.setStatus(SMSJobStatus.COMPLETED);
        smsJob.setCompletedAt(LocalDateTime.now());
        smsJobRepository.save(smsJob);

        // 8. Append WalletLedger entry for SMS earnings
        WalletLedger smsEarning = new WalletLedger();
        smsEarning.setUser(user);
        smsEarning.setAmount(smsEarningRate);
        smsEarning.setLedgerType(LedgerType.SMS_EARNING);
        smsEarning.setDescription("SMS job completion earnings");
        smsEarning.setReferenceId(jobId);
        walletLedgerRepository.save(smsEarning);

        // 9. Calculate and distribute referral rewards (3 levels)
        distributeReferralRewards(user, smsEarningRate, jobId);
    }

    /**
     * Distributes referral rewards to up to 3 levels of referrers.
     * Level 1: 10%, Level 2: 2%, Level 3: 1%
     * 
     * @param user the user who completed the SMS job
     * @param baseAmount the base earning amount from the SMS job
     * @param jobId the job ID for reference
     */
    private void distributeReferralRewards(User user, BigDecimal baseAmount, UUID jobId) {
        // Pre-calculate reward amounts for all levels
        BigDecimal[] rewardAmounts = {
            baseAmount.multiply(LEVEL_1_PERCENTAGE).setScale(2, RoundingMode.HALF_UP),
            baseAmount.multiply(LEVEL_2_PERCENTAGE).setScale(2, RoundingMode.HALF_UP),
            baseAmount.multiply(LEVEL_3_PERCENTAGE).setScale(2, RoundingMode.HALF_UP)
        };
        String[] levelDescriptions = {"Level 1", "Level 2", "Level 3"};

        User currentUser = user;
        for (int level = 0; level < 3; level++) {
            // Get referrer
            User referrer = currentUser.getReferrer();
            if (referrer == null) {
                // No more referrers in the chain
                break;
            }

            // Append WalletLedger entry for referral bonus
            WalletLedger referralBonus = new WalletLedger();
            referralBonus.setUser(referrer);
            referralBonus.setAmount(rewardAmounts[level]);
            referralBonus.setLedgerType(LedgerType.REFERRAL_BONUS);
            referralBonus.setDescription("Referral bonus - " + levelDescriptions[level] + " from user " + user.getUsername());
            referralBonus.setReferenceId(jobId);
            walletLedgerRepository.save(referralBonus);

            // Move to next level
            currentUser = referrer;
        }
    }
}
