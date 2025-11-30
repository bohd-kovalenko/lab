package com.quaterion.security

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

class JwtUtilTest {

    private lateinit var jwtUtil: JwtUtil

    @BeforeEach
    fun setUp() {
        jwtUtil = JwtUtil()
        ReflectionTestUtils.setField(
            jwtUtil,
            "secret",
            "yourSecretKeyMustBeAtLeast256BitsLongForHS256AlgorithmToWorkProperly"
        )
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L) // 24 hours
    }

    @Test
    fun `should generate valid JWT token`() {
        val username = "testuser"

        val token = jwtUtil.generateToken(username)

        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        assertTrue(token.split(".").size == 3) // JWT has 3 parts: header.payload.signature
    }

    @Test
    fun `should extract username from token`() {
        val username = "testuser"
        val token = jwtUtil.generateToken(username)

        val extractedUsername = jwtUtil.extractUsername(token)

        assertEquals(username, extractedUsername)
    }

    @Test
    fun `should extract expiration from token`() {
        val username = "testuser"
        val token = jwtUtil.generateToken(username)

        val expiration = jwtUtil.extractExpiration(token)

        assertNotNull(expiration)
        assertTrue(expiration.after(Date()))
    }

    @Test
    fun `should validate token with correct username`() {
        val username = "testuser"
        val token = jwtUtil.generateToken(username)
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns username

        val isValid = jwtUtil.validateToken(token, userDetails)

        assertTrue(isValid)
    }

    @Test
    fun `should invalidate token with incorrect username`() {
        val token = jwtUtil.generateToken("testuser")
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns "differentuser"

        val isValid = jwtUtil.validateToken(token, userDetails)

        assertFalse(isValid)
    }

    @Test
    fun `should invalidate expired token`() {
        val expiredJwtUtil = JwtUtil()
        ReflectionTestUtils.setField(
            expiredJwtUtil,
            "secret",
            "yourSecretKeyMustBeAtLeast256BitsLongForHS256AlgorithmToWorkProperly"
        )
        ReflectionTestUtils.setField(expiredJwtUtil, "expiration", -1000L) // Expired immediately

        val username = "testuser"
        val token = expiredJwtUtil.generateToken(username)
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns username

        Thread.sleep(10)

        assertThrows(io.jsonwebtoken.ExpiredJwtException::class.java) {
            expiredJwtUtil.validateToken(token, userDetails)
        }
    }

    @Test
    fun `should generate different tokens for different users`() {
        val username1 = "user1"
        val username2 = "user2"

        val token1 = jwtUtil.generateToken(username1)
        val token2 = jwtUtil.generateToken(username2)

        assertNotEquals(token1, token2)
    }

    @Test
    fun `should extract custom claims using claimsResolver`() {
        val username = "testuser"
        val token = jwtUtil.generateToken(username)

        val subject = jwtUtil.extractClaim(token) { it.subject }
        val issuedAt = jwtUtil.extractClaim(token) { it.issuedAt }

        assertEquals(username, subject)
        assertNotNull(issuedAt)
        assertTrue(issuedAt.before(Date()) || issuedAt.equals(Date()))
    }

    @Test
    fun `should fail to parse invalid token`() {
        val invalidToken = "invalid.token.here"
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns "testuser"

        assertThrows(Exception::class.java) {
            jwtUtil.validateToken(invalidToken, userDetails)
        }
    }

    @Test
    fun `should fail to extract username from invalid token`() {
        val invalidToken = "invalid.token.here"

        assertThrows(Exception::class.java) {
            jwtUtil.extractUsername(invalidToken)
        }
    }

    @Test
    fun `should generate token with correct expiration time`() {
        val username = "testuser"
        val beforeGeneration = System.currentTimeMillis()

        val token = jwtUtil.generateToken(username)
        val expiration = jwtUtil.extractExpiration(token)
        val afterGeneration = System.currentTimeMillis()

        val expectedMinExpiration = beforeGeneration + 86400000L
        val expectedMaxExpiration = afterGeneration + 86400000L
        val actualExpiration = expiration.time

        assertTrue(actualExpiration >= expectedMinExpiration - 1000) // Allow 1 second tolerance
        assertTrue(actualExpiration <= expectedMaxExpiration + 1000) // Allow 1 second tolerance
    }
}
