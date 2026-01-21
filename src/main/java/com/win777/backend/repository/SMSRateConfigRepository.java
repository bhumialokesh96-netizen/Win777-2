package com.win777.backend.repository;

import com.win777.backend.entity.SMSRateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for SMSRateConfig entity.
 * Provides standard CRUD operations through JpaRepository.
 */
@Repository
public interface SMSRateConfigRepository extends JpaRepository<SMSRateConfig, UUID> {
}
