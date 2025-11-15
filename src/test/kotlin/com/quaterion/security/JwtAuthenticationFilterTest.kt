package com.quaterion.security

import io.mockk.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class JwtAuthenticationFilterTest {

    private lateinit var jwtUtil: JwtUtil
    private lateinit var userDetailsService: UserDetailsService
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    private lateinit var filterChain: FilterChain

    @BeforeEach
    fun setUp() {
        jwtUtil = mockk()
        userDetailsService = mockk()
        jwtAuthenticationFilter = JwtAuthenticationFilter(jwtUtil, userDetailsService)
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        filterChain = mockk(relaxed = true)
        SecurityContextHolder.clearContext()
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should authenticate user with valid JWT token`() {
        val token = "valid.jwt.token"
        val username = "testuser"
        val userDetails = mockk<UserDetails>(relaxed = true)

        request.addHeader("Authorization", "Bearer $token")
        every { jwtUtil.extractUsername(token) } returns username
        every { userDetailsService.loadUserByUsername(username) } returns userDetails
        every { jwtUtil.validateToken(token, userDetails) } returns true
        every { userDetails.authorities } returns emptyList()

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify { jwtUtil.extractUsername(token) }
        verify { userDetailsService.loadUserByUsername(username) }
        verify { jwtUtil.validateToken(token, userDetails) }
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should not authenticate when Authorization header is missing`() {

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { jwtUtil.extractUsername(any()) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should not authenticate when Authorization header does not start with Bearer`() {
        request.addHeader("Authorization", "Basic sometoken")

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { jwtUtil.extractUsername(any()) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should not authenticate when token is invalid`() {
        val token = "invalid.jwt.token"
        val username = "testuser"
        val userDetails = mockk<UserDetails>(relaxed = true)

        request.addHeader("Authorization", "Bearer $token")
        every { jwtUtil.extractUsername(token) } returns username
        every { userDetailsService.loadUserByUsername(username) } returns userDetails
        every { jwtUtil.validateToken(token, userDetails) } returns false

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify { jwtUtil.extractUsername(token) }
        verify { userDetailsService.loadUserByUsername(username) }
        verify { jwtUtil.validateToken(token, userDetails) }
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should handle exception when extracting username from token`() {
        val token = "malformed.token"
        request.addHeader("Authorization", "Bearer $token")
        every { jwtUtil.extractUsername(token) } throws RuntimeException("Invalid token")

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify { jwtUtil.extractUsername(token) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should not authenticate when SecurityContext already has authentication`() {
        val token = "valid.jwt.token"
        val username = "testuser"
        val authentication = mockk<org.springframework.security.core.Authentication>()

        SecurityContextHolder.getContext().authentication = authentication

        request.addHeader("Authorization", "Bearer $token")
        every { jwtUtil.extractUsername(token) } returns username

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify { jwtUtil.extractUsername(token) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should continue filter chain in all cases`() {

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should handle Bearer token with exact format`() {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature"
        val username = "testuser"
        val userDetails = mockk<UserDetails>(relaxed = true)

        request.addHeader("Authorization", "Bearer $token")
        every { jwtUtil.extractUsername(token) } returns username
        every { userDetailsService.loadUserByUsername(username) } returns userDetails
        every { jwtUtil.validateToken(token, userDetails) } returns true
        every { userDetails.authorities } returns emptyList()

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        verify { jwtUtil.extractUsername(token) }
        verify { userDetailsService.loadUserByUsername(username) }
    }
}
