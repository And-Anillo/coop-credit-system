package com.coopcredit.credit_application_service.domain.port.output;

import com.coopcredit.credit_application_service.domain.model.User;

import java.util.Optional;

/**
 * Output Port: UserRepository
 * Contract for persisting and retrieving users
 */
public interface UserRepository {

    /**
     * Find a user by username
     *
     * @param username the user's username
     * @return the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Save a user
     *
     * @param user the user to save
     * @return the saved user with ID
     */
    User save(User user);

    /**
     * Check if a user exists by username
     *
     * @param username the username
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
}
