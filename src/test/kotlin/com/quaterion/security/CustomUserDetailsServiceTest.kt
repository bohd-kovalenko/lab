package com.quaterion.security

import com.quaterion.entity.User
import com.quaterion.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException

class CustomUserDetailsServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        customUserDetailsService = CustomUserDetailsService(userRepository)
    }

    @Test
    fun `should load user by username successfully`() {
        val username = "testuser"
        val user = User(id = 1L, username = username, password = "password123", role = "USER")
        every { userRepository.findByUsername(username) } returns user

        val userDetails = customUserDetailsService.loadUserByUsername(username)

        assertNotNull(userDetails)
        assertEquals(username, userDetails.username)
        assertEquals("password123", userDetails.password)
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    fun `should throw UsernameNotFoundException when user not found`() {
        val username = "nonexistent"
        every { userRepository.findByUsername(username) } returns null

        val exception = assertThrows(UsernameNotFoundException::class.java) {
            customUserDetailsService.loadUserByUsername(username)
        }

        assertEquals("User not found: $username", exception.message)
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    fun `should return CustomUserDetails instance`() {
        val username = "testuser"
        val user = User(id = 1L, username = username, password = "password123", role = "ADMIN")
        every { userRepository.findByUsername(username) } returns user

        val userDetails = customUserDetailsService.loadUserByUsername(username)

        assertTrue(userDetails is CustomUserDetails)
    }

    @Test
    fun `should load user with correct authorities`() {
        val username = "admin"
        val user = User(id = 1L, username = username, password = "password123", role = "ADMIN")
        every { userRepository.findByUsername(username) } returns user

        val userDetails = customUserDetailsService.loadUserByUsername(username)

        assertEquals(1, userDetails.authorities.size)
        assertTrue(userDetails.authorities.any { it.authority == "ROLE_ADMIN" })
    }

    @Test
    fun `should handle empty username`() {
        val username = ""
        every { userRepository.findByUsername(username) } returns null

        assertThrows(UsernameNotFoundException::class.java) {
            customUserDetailsService.loadUserByUsername(username)
        }
    }
}
