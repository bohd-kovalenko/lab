package com.quaterion.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entity representing a vehicle in the system.
 *
 * A vehicle belongs to a user and can have multiple refueling records.
 *
 * @property id unique identifier, auto-generated
 * @property user owner of the vehicle
 * @property make vehicle manufacturer (e.g., "Toyota", "Ford")
 * @property model vehicle model name
 * @property year manufacturing year
 * @property licensePlate optional unique license plate number
 * @property fuelType type of fuel (GASOLINE, DIESEL, ELECTRIC, HYBRID)
 * @property tankCapacity fuel tank capacity in liters
 * @property createdAt timestamp when the vehicle was created
 * @property updatedAt timestamp of the last update
 */
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
    val fuelType: String,

    @Column(nullable = false)
    val tankCapacity: Double,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
