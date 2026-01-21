package com.win777.backend.repository;

import com.win777.backend.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Withdrawal entity.
 * Provides standard CRUD operations through JpaRepository.
 */
@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, UUID> {
}
