package com.win777.backend.controller;

import com.win777.backend.dto.LeaderboardEntry;
import com.win777.backend.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for leaderboard operations.
 * Provides read-only access to user rankings.
 */
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /**
     * Gets the weekly leaderboard (top earners in the last 7 days).
     * 
     * @param limit the number of top users to return (default: 50, max: 100)
     * @return list of leaderboard entries
     */
    @GetMapping("/weekly")
    public ResponseEntity<List<LeaderboardEntry>> getWeeklyLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        
        // Enforce maximum limit
        if (limit > 100) {
            limit = 100;
        }
        
        List<LeaderboardEntry> leaderboard = leaderboardService.getWeeklyLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }

    /**
     * Gets the monthly leaderboard (top earners in the last 30 days).
     * 
     * @param limit the number of top users to return (default: 50, max: 100)
     * @return list of leaderboard entries
     */
    @GetMapping("/monthly")
    public ResponseEntity<List<LeaderboardEntry>> getMonthlyLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        
        // Enforce maximum limit
        if (limit > 100) {
            limit = 100;
        }
        
        List<LeaderboardEntry> leaderboard = leaderboardService.getMonthlyLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }
}
