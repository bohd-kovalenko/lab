package com.quaterion.security

import com.quaterion.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Custom implementation of Spring Security's [UserDetailsService].
 *
 * Loads user details from the database for authentication.
 *
 * @property userRepository repository for user data access
 */
@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * Loads a user by username for authentication.
     *
     * @param username the username to search for
     * @return [UserDetails] containing the user's authentication information
     * @throws UsernameNotFoundException if no user with the given username exists
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")
        return CustomUserDetails(user)
    }
}
