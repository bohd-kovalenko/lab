package com.quaterion.dto

/**
 * Response DTO for successful authentication.
 *
 * @property token JWT token for subsequent authenticated requests
 * @property username the authenticated user's username
 * @property message status message (default: "Authentication successful")
 */
data class AuthResponse(
    val token: String,
    val username: String,
    val message: String = "Authentication successful"
)
