package com.quaterion.repository

import com.quaterion.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for [User] entity data access.
 *
 * Provides methods for user lookup and existence checks.
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return the [User] if found, null otherwise
     */
    fun findByUsername(username: String): User?

    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    fun existsByUsername(username: String): Boolean
}
