package com.win777.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for leaderboard entry.
 */
public class LeaderboardEntry {
    
    private UUID userId;
    private String username;
    private BigDecimal totalEarnings;
    private int rank;

    public LeaderboardEntry() {}

    public LeaderboardEntry(UUID userId, String username, BigDecimal totalEarnings, int rank) {
        this.userId = userId;
        this.username = username;
        this.totalEarnings = totalEarnings;
        this.rank = rank;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
