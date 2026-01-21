package com.win777.backend.repository;

import com.win777.backend.entity.SMSJob;
import com.win777.backend.enums.SMSJobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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
     * Uses enum constant to avoid magic strings and PageRequest to limit results.
     * 
     * @param status the job status to filter by (PENDING)
     * @param pageable pagination to limit to 1 result
     * @return List containing at most one pending job, or empty if none available
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM SMSJob j WHERE j.status = :status ORDER BY j.createdAt ASC")
    List<SMSJob> findByStatusOrderByCreatedAtAsc(SMSJobStatus status, PageRequest pageable);
    
    /**
     * Convenience method to find the first PENDING job with FOR UPDATE locking.
     * 
     * @return Optional containing the first pending job, or empty if none available
     */
    default Optional<SMSJob> findFirstPendingJobForUpdate() {
        List<SMSJob> jobs = findByStatusOrderByCreatedAtAsc(SMSJobStatus.PENDING, PageRequest.of(0, 1));
        return jobs.isEmpty() ? Optional.empty() : Optional.of(jobs.get(0));
    }
}
