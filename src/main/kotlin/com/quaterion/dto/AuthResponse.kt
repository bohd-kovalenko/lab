package com.quaterion.dto

data class AuthResponse(
    val token: String,
    val username: String,
    val message: String = "Authentication successful"
)
