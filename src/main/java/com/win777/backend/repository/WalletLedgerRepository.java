package com.win777.backend.repository;

import com.win777.backend.entity.WalletLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
    // Append-only ledger - inherits save() for new entries only
    // No custom save/update/delete methods needed
}
