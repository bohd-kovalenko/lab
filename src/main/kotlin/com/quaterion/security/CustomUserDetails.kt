package com.quaterion.security

import com.quaterion.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Custom implementation of Spring Security's [UserDetails].
 *
 * Wraps a [User] entity to provide user details for authentication
 * and authorization, including the user's ID for data access control.
 *
 * @property user the wrapped User entity
 */
class CustomUserDetails(private val user: User) : UserDetails {

    /**
     * Returns the authorities granted to the user.
     *
     * @return collection containing a single authority based on the user's role
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
    }

    /**
     * Returns the user's encrypted password.
     *
     * @return the BCrypt-hashed password
     */
    override fun getPassword(): String {
        return user.password
    }

    /**
     * Returns the username used for authentication.
     *
     * @return the username
     */
    override fun getUsername(): String {
        return user.username
    }

    /** @return true (accounts never expire) */
    override fun isAccountNonExpired(): Boolean = true

    /** @return true (accounts are never locked) */
    override fun isAccountNonLocked(): Boolean = true

    /** @return true (credentials never expire) */
    override fun isCredentialsNonExpired(): Boolean = true

    /** @return true (all accounts are enabled) */
    override fun isEnabled(): Boolean = true

    /**
     * Returns the user's database ID.
     *
     * Used for ownership verification in service layer.
     *
     * @return the user's ID, or null if not persisted
     */
    fun getUserId(): Long? = user.id
}
