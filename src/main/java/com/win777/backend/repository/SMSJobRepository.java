package com.win777.backend.repository;

import com.win777.backend.entity.SMSJob;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for SMSJob entity.
 * Provides standard CRUD operations and custom queries for job management.
 */
@Repository
public interface SMSJobRepository extends JpaRepository<SMSJob, UUID> {
    
    /**
     * Finds the first PENDING job with database-level locking (FOR UPDATE).
     * This ensures atomicity when claiming jobs in a concurrent environment.
     * 
     * @return Optional containing the first pending job, or empty if none available
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM SMSJob j WHERE j.status = 'PENDING' ORDER BY j.createdAt ASC")
    Optional<SMSJob> findFirstPendingJobForUpdate();
}
