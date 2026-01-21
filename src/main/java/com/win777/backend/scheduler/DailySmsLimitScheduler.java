package com.win777.backend.scheduler;

import com.win777.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Scheduler for daily SMS limit reset.
 * Resets all users' daily SMS counters at midnight.
 */
@Component
public class DailySmsLimitScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DailySmsLimitScheduler.class);

    private final UserRepository userRepository;

    public DailySmsLimitScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Resets daily SMS counters for all users at midnight.
     * Cron expression: 0 0 0 * * * (runs at 00:00:00 every day)
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailySmsCounts() {
        logger.info("Starting daily SMS count reset at {}", LocalDate.now());
        
        try {
            // Reset all users' daily SMS count to 0 and update last reset date
            int updatedCount = userRepository.resetDailySmsCounts(LocalDate.now());
            
            logger.info("Successfully reset daily SMS counts for {} users", updatedCount);
        } catch (Exception e) {
            logger.error("Error resetting daily SMS counts", e);
            // Log error but don't throw - scheduler will try again tomorrow
        }
    }
}
