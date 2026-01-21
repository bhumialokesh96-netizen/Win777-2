package com.win777.backend.repository;

import com.win777.backend.entity.Withdrawal;
import com.win777.backend.enums.WithdrawalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Withdrawal entity.
 * Provides standard CRUD operations through JpaRepository.
 */
@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, UUID> {
    
    /**
     * Finds all withdrawals for a user, ordered by creation date descending.
     * 
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return page of withdrawals
     */
    Page<Withdrawal> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Finds withdrawals by user and status, ordered by creation date descending.
     * 
     * @param userId the user ID
     * @param status the withdrawal status
     * @param pageable pagination parameters
     * @return page of withdrawals
     */
    Page<Withdrawal> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, WithdrawalStatus status, Pageable pageable);
}
