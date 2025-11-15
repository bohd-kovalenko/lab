package com.quaterion.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class CreateRefuelingRequest(
    @field:NotNull(message = "Vehicle ID is required")
    val vehicleId: Long,

    @field:NotNull(message = "Date is required")
    val date: LocalDateTime,

    @field:Positive(message = "Odometer reading must be positive")
    val odometer: Double,

    @field:Positive(message = "Fuel amount must be positive")
    val fuelAmount: Double,

    @field:Positive(message = "Price per liter must be positive")
    val pricePerLiter: Double,

    val notes: String? = null,

    @field:NotNull(message = "Full tank indicator is required")
    val fullTank: Boolean = false
)

data class UpdateRefuelingRequest(
    val date: LocalDateTime? = null,
    @field:Positive(message = "Odometer reading must be positive")
    val odometer: Double? = null,
    @field:Positive(message = "Fuel amount must be positive")
    val fuelAmount: Double? = null,
    @field:Positive(message = "Price per liter must be positive")
    val pricePerLiter: Double? = null,
    val notes: String? = null,
    val fullTank: Boolean? = null
)

data class RefuelingResponse(
    val id: Long,
    val vehicleId: Long,
    val date: LocalDateTime,
    val odometer: Double,
    val fuelAmount: Double,
    val pricePerLiter: Double,
    val totalCost: Double,
    val notes: String?,
    val fullTank: Boolean,
    val fuelConsumption: Double?, // L/100km (null if cannot be calculated)
    val distanceSinceLastRefueling: Double?, // km (null if this is the first refueling)
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class FuelStatisticsResponse(
    val vehicleId: Long,
    val totalRefuelings: Int,
    val totalFuelAmount: Double,
    val totalCost: Double,
    val averageConsumption: Double?, // L/100km
    val averagePricePerLiter: Double,
    val totalDistance: Double,
    val periodStart: LocalDateTime?,
    val periodEnd: LocalDateTime?
)

data class ConsumptionGraphDataPoint(
    val date: LocalDateTime,
    val fuelConsumption: Double, // L/100km
    val odometer: Double
)

data class ConsumptionGraphResponse(
    val vehicleId: Long,
    val dataPoints: List<ConsumptionGraphDataPoint>,
    val averageConsumption: Double?
)

data class CostGraphDataPoint(
    val date: LocalDateTime,
    val totalCost: Double,
    val pricePerLiter: Double
)

data class CostGraphResponse(
    val vehicleId: Long,
    val dataPoints: List<CostGraphDataPoint>,
    val totalCost: Double,
    val averagePricePerLiter: Double
)
