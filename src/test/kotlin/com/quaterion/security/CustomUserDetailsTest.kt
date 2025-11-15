package com.quaterion.security

import com.quaterion.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority

class CustomUserDetailsTest {

    @Test
    fun `should return correct username`() {
        val user = User(id = 1L, username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val username = userDetails.username

        assertEquals("testuser", username)
    }

    @Test
    fun `should return correct password`() {
        val user = User(id = 1L, username = "testuser", password = "encodedPassword", role = "USER")
        val userDetails = CustomUserDetails(user)

        val password = userDetails.password

        assertEquals("encodedPassword", password)
    }

    @Test
    fun `should return authorities with ROLE prefix for USER role`() {
        val user = User(id = 1L, username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val authorities = userDetails.authorities

        assertEquals(1, authorities.size)
        assertTrue(authorities.contains(SimpleGrantedAuthority("ROLE_USER")))
    }

    @Test
    fun `should return authorities with ROLE prefix for ADMIN role`() {
        val user = User(id = 1L, username = "admin", password = "password123", role = "ADMIN")
        val userDetails = CustomUserDetails(user)

        val authorities = userDetails.authorities

        assertEquals(1, authorities.size)
        assertTrue(authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")))
    }

    @Test
    fun `should return true for isAccountNonExpired`() {
        val user = User(id = 1L, username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val isAccountNonExpired = userDetails.isAccountNonExpired

        assertTrue(isAccountNonExpired)
    }

    @Test
    fun `should return true for isAccountNonLocked`() {
        val user = User(id = 1L, username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val isAccountNonLocked = userDetails.isAccountNonLocked

        assertTrue(isAccountNonLocked)
    }

    @Test
    fun `should return true for isCredentialsNonExpired`() {
        val user = User(id = 1L, username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val isCredentialsNonExpired = userDetails.isCredentialsNonExpired

        assertTrue(isCredentialsNonExpired)
    }

    @Test
    fun `should return true for isEnabled`() {
        val user = User(id = 1L, username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val isEnabled = userDetails.isEnabled

        assertTrue(isEnabled)
    }

    @Test
    fun `should return correct user id`() {
        val user = User(id = 42L, username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val userId = userDetails.getUserId()

        assertEquals(42L, userId)
    }

    @Test
    fun `should return null user id when user has no id`() {
        val user = User(username = "testuser", password = "password123", role = "USER")
        val userDetails = CustomUserDetails(user)

        val userId = userDetails.getUserId()

        assertNull(userId)
    }
}
