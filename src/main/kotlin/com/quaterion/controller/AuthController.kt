package com.quaterion.controller

import com.quaterion.dto.AuthRequest
import com.quaterion.dto.AuthResponse
import com.quaterion.entity.User
import com.quaterion.repository.UserRepository
import com.quaterion.security.JwtUtil
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

/**
 * REST controller for user authentication operations.
 *
 * Handles user registration and login endpoints with JWT token generation.
 *
 * @property authenticationManager Spring Security authentication manager
 * @property userRepository repository for user data access
 * @property jwtUtil utility for JWT token operations
 * @property passwordEncoder encoder for password hashing
 */
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Registers a new user in the system.
     *
     * Creates a new user account with encrypted password and returns a JWT token.
     *
     * @param request authentication request containing username and password
     * @return [ResponseEntity] with [AuthResponse] on success (HTTP 201),
     *         or error message if username already exists (HTTP 409)
     */
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: AuthRequest): ResponseEntity<Any> {
        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapOf("error" to "Username already exists"))
        }

        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            role = "USER"
        )

        userRepository.save(user)

        val token = jwtUtil.generateToken(user.username)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AuthResponse(token, user.username, "Registration successful"))
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * Validates user credentials and generates a new JWT token on successful authentication.
     *
     * @param request authentication request containing username and password
     * @return [ResponseEntity] with [AuthResponse] containing JWT token on success (HTTP 200),
     *         or error message on invalid credentials (HTTP 401)
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: AuthRequest): ResponseEntity<Any> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )

            val token = jwtUtil.generateToken(request.username)

            ResponseEntity.ok(AuthResponse(token, request.username))
        } catch (e: AuthenticationException) {
            ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid username or password"))
        }
    }
}
