package com.coopcredit.credit_application_service.infrastructure.persistence.repository;

import com.coopcredit.credit_application_service.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository: UserJpaRepository
 * Provides database operations for UserEntity
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Check if a user exists by username
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
}
