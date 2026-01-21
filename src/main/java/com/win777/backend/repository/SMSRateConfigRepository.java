package com.win777.backend.repository;

import com.win777.backend.entity.SMSRateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for SMSRateConfig entity.
 * Provides standard CRUD operations through JpaRepository.
 */
@Repository
public interface SMSRateConfigRepository extends JpaRepository<SMSRateConfig, UUID> {
    
    /**
     * Finds the active SMS rate configuration.
     * 
     * @param isActive the active flag (true for active configuration)
     * @return Optional containing the active configuration, or empty if none found
     */
    Optional<SMSRateConfig> findByIsActive(Boolean isActive);
}
