package com.quaterion.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entity representing a refueling event for a vehicle.
 *
 * Stores information about fuel purchases including amount, price,
 * and odometer reading for consumption tracking.
 *
 * @property id unique identifier, auto-generated
 * @property vehicle the vehicle that was refueled
 * @property date date and time of refueling
 * @property odometer current odometer reading in kilometers
 * @property fuelAmount amount of fuel added in liters
 * @property pricePerLiter fuel price per liter
 * @property totalCost total cost of refueling (fuelAmount * pricePerLiter)
 * @property notes optional user notes about the refueling
 * @property fullTank whether the tank was filled completely (required for consumption calculation)
 * @property createdAt timestamp when the record was created
 * @property updatedAt timestamp of the last update
 */
@Entity
@Table(name = "refuelings")
data class Refueling(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    val vehicle: Vehicle,

    @Column(nullable = false)
    val date: LocalDateTime,

    @Column(nullable = false)
    val odometer: Double,

    @Column(nullable = false)
    val fuelAmount: Double,

    @Column(nullable = false)
    val pricePerLiter: Double,

    @Column(nullable = false)
    val totalCost: Double,

    @Column
    val notes: String? = null,

    @Column(nullable = false)
    val fullTank: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
