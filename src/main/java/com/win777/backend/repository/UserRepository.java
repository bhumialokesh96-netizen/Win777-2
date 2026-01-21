package com.win777.backend.repository;

import com.win777.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity.
 * Provides standard CRUD operations through JpaRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    Optional<User> findByReferralCode(String referralCode);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Resets daily SMS count for all users and updates last reset date.
     * 
     * @param resetDate the date to set as last reset date
     * @return the number of users updated
     */
    @Modifying
    @Query("UPDATE User u SET u.dailySmsSentCount = 0, u.lastSmsResetDate = :resetDate")
    int resetDailySmsCounts(@Param("resetDate") LocalDate resetDate);
}
