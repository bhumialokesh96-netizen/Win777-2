package com.win777.backend.repository;

import com.win777.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for User entity.
 * Provides standard CRUD operations through JpaRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
