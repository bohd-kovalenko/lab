package com.quaterion.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "vehicles")
data class Vehicle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val make: String,

    @Column(nullable = false)
    val model: String,

    @Column(name = "\"year\"", nullable = false)
    val year: Int,

    @Column(unique = true)
    val licensePlate: String? = null,

    @Column(nullable = false)
    val fuelType: String, // e.g., "GASOLINE", "DIESEL", "ELECTRIC", "HYBRID"

    @Column(nullable = false)
    val tankCapacity: Double, // in liters

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
