package com.win777.backend.service;

import com.win777.backend.dto.LeaderboardEntry;
import com.win777.backend.entity.User;
import com.win777.backend.enums.LedgerType;
import com.win777.backend.repository.UserRepository;
import com.win777.backend.repository.WalletLedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for leaderboard operations.
 * Provides read-only access to user rankings based on earnings.
 */
@Service
public class LeaderboardService {

    private final WalletLedgerRepository walletLedgerRepository;
    private final UserRepository userRepository;

    public LeaderboardService(WalletLedgerRepository walletLedgerRepository, UserRepository userRepository) {
        this.walletLedgerRepository = walletLedgerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets the weekly leaderboard (top earners in the last 7 days).
     * 
     * @param limit the number of top users to return
     * @return list of leaderboard entries
     */
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getWeeklyLeaderboard(int limit) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        
        // Query wallet ledger for earnings in the last week
        List<Object[]> results = walletLedgerRepository.findTopEarnersByPeriod(weekAgo, limit);
        
        return buildLeaderboard(results);
    }

    /**
     * Gets the monthly leaderboard (top earners in the last 30 days).
     * 
     * @param limit the number of top users to return
     * @return list of leaderboard entries
     */
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getMonthlyLeaderboard(int limit) {
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        
        // Query wallet ledger for earnings in the last month
        List<Object[]> results = walletLedgerRepository.findTopEarnersByPeriod(monthAgo, limit);
        
        return buildLeaderboard(results);
    }

    /**
     * Builds leaderboard entries from query results.
     * Uses batch fetch to avoid N+1 query problem.
     * 
     * @param results query results containing userId and total earnings
     * @return list of leaderboard entries with ranks
     */
    private List<LeaderboardEntry> buildLeaderboard(List<Object[]> results) {
        if (results.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Extract user IDs
        List<java.util.UUID> userIds = results.stream()
                .map(result -> (java.util.UUID) result[0])
                .collect(Collectors.toList());
        
        // Batch fetch all users in one query
        Map<java.util.UUID, String> userIdToUsername = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        
        // Build leaderboard entries
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        int rank = 1;
        
        for (Object[] result : results) {
            java.util.UUID userId = (java.util.UUID) result[0];
            BigDecimal totalEarnings = (BigDecimal) result[1];
            String username = userIdToUsername.getOrDefault(userId, "Unknown");
            
            LeaderboardEntry entry = new LeaderboardEntry(userId, username, totalEarnings, rank++);
            leaderboard.add(entry);
        }
        
        return leaderboard;
    }
}
