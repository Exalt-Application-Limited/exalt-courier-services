package com.microecosystem.courier.driver.app.repository;

import com.microecosystem.courier.driver.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username.
     *
     * @param username username
     * @return optional user
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by email.
     *
     * @param email email
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a username exists.
     *
     * @param username username
     * @return true if exists
     */
    Boolean existsByUsername(String username);

    /**
     * Check if an email exists.
     *
     * @param email email
     * @return true if exists
     */
    Boolean existsByEmail(String email);

    /**
     * Find a user by password reset token.
     *
     * @param token password reset token
     * @return optional user
     */
    Optional<User> findByPasswordResetToken(String token);

    /**
     * Find a user by verification token.
     *
     * @param token verification token
     * @return optional user
     */
    Optional<User> findByVerificationToken(String token);
} 