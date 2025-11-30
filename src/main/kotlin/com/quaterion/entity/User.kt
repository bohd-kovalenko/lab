package com.quaterion.entity

import jakarta.persistence.*

/**
 * Entity representing a user in the system.
 *
 * Users can own multiple vehicles and are authenticated using JWT tokens.
 *
 * @property id unique identifier, auto-generated
 * @property username unique login name
 * @property password BCrypt-hashed password
 * @property role user role for authorization (default: "USER")
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val role: String = "USER"
)
