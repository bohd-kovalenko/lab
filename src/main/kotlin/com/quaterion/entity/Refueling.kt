package com.quaterion.entity

import jakarta.persistence.*
import java.time.LocalDateTime

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
    val odometer: Double, // Current odometer reading in km

    @Column(nullable = false)
    val fuelAmount: Double, // Amount of fuel added in liters

    @Column(nullable = false)
    val pricePerLiter: Double,

    @Column(nullable = false)
    val totalCost: Double,

    @Column
    val notes: String? = null,

    @Column(nullable = false)
    val fullTank: Boolean = false, // Whether the tank was filled completely

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
