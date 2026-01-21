package com.win777.backend.repository;

import com.win777.backend.entity.WalletLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for WalletLedger entity.
 * Provides read-only operations for the append-only ledger.
 * 
 * Note: WalletLedger follows append-only pattern - no update or delete operations.
 * Use save() only to append new ledger entries.
 */
@Repository
public interface WalletLedgerRepository extends JpaRepository<WalletLedger, UUID> {
    
    /**
     * Calculates the wallet balance for a user by summing all ledger entries.
     * 
     * @param userId the user ID
     * @return the wallet balance
     */
    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM WalletLedger w WHERE w.user.id = :userId")
    BigDecimal calculateBalance(UUID userId);
    
    /**
     * Finds all wallet ledger entries for a user, ordered by creation date descending.
     * 
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return page of wallet ledger entries
     */
    @Query("SELECT w FROM WalletLedger w WHERE w.user.id = :userId ORDER BY w.createdAt DESC")
    Page<WalletLedger> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Finds top earners by period for leaderboard.
     * Returns a limited number of top earners sorted by total earnings.
     * 
     * @param startDate the start date of the period
     * @return list of user IDs and their total earnings
     */
    @Query(value = "SELECT w.user_id, SUM(w.amount) as total_earnings " +
           "FROM wallet_ledger w " +
           "WHERE w.created_at >= :startDate AND w.amount > 0 " +
           "GROUP BY w.user_id " +
           "ORDER BY total_earnings DESC " +
           "LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopEarnersByPeriod(@Param("startDate") LocalDateTime startDate, 
                                          @Param("limit") int limit);
}
