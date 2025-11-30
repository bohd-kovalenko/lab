package com.quaterion.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

/**
 * Request DTO for creating a new refueling record.
 *
 * @property vehicleId ID of the vehicle being refueled
 * @property date date and time of the refueling
 * @property odometer current odometer reading in kilometers
 * @property fuelAmount amount of fuel added in liters
 * @property pricePerLiter fuel price per liter
 * @property notes optional notes about the refueling
 * @property fullTank whether the tank was filled completely (required for consumption calculation)
 */
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

/**
 * Request DTO for updating an existing refueling record.
 *
 * All fields are optional - only provided fields will be updated.
 *
 * @property date date and time of the refueling
 * @property odometer odometer reading in kilometers
 * @property fuelAmount amount of fuel in liters
 * @property pricePerLiter fuel price per liter
 * @property notes notes about the refueling
 * @property fullTank whether the tank was filled completely
 */
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

/**
 * Response DTO for refueling record data.
 *
 * @property id unique identifier
 * @property vehicleId ID of the associated vehicle
 * @property date date and time of refueling
 * @property odometer odometer reading in kilometers
 * @property fuelAmount amount of fuel added in liters
 * @property pricePerLiter fuel price per liter
 * @property totalCost total cost of refueling
 * @property notes optional notes
 * @property fullTank whether the tank was filled completely
 * @property fuelConsumption calculated fuel consumption in L/100km (null if cannot be calculated)
 * @property distanceSinceLastRefueling distance traveled since last refueling in km (null for first refueling)
 * @property createdAt timestamp when the record was created
 * @property updatedAt timestamp of the last update
 */
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
    val fuelConsumption: Double?,
    val distanceSinceLastRefueling: Double?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Response DTO for fuel statistics.
 *
 * @property vehicleId ID of the vehicle
 * @property totalRefuelings total number of refueling records
 * @property totalFuelAmount total fuel consumed in liters
 * @property totalCost total cost of all refuelings
 * @property averageConsumption average fuel consumption in L/100km (null if insufficient data)
 * @property averagePricePerLiter weighted average price per liter
 * @property totalDistance total distance traveled in kilometers
 * @property periodStart date of the first refueling in the period
 * @property periodEnd date of the last refueling in the period
 */
data class FuelStatisticsResponse(
    val vehicleId: Long,
    val totalRefuelings: Int,
    val totalFuelAmount: Double,
    val totalCost: Double,
    val averageConsumption: Double?,
    val averagePricePerLiter: Double,
    val totalDistance: Double,
    val periodStart: LocalDateTime?,
    val periodEnd: LocalDateTime?
)

/**
 * Data point for fuel consumption graph.
 *
 * @property date date of the refueling
 * @property fuelConsumption fuel consumption in L/100km
 * @property odometer odometer reading at this point
 */
data class ConsumptionGraphDataPoint(
    val date: LocalDateTime,
    val fuelConsumption: Double,
    val odometer: Double
)

/**
 * Response DTO for fuel consumption graph data.
 *
 * @property vehicleId ID of the vehicle
 * @property dataPoints list of consumption data points
 * @property averageConsumption average consumption across all data points in L/100km
 */
data class ConsumptionGraphResponse(
    val vehicleId: Long,
    val dataPoints: List<ConsumptionGraphDataPoint>,
    val averageConsumption: Double?
)

/**
 * Data point for cost analysis graph.
 *
 * @property date date of the refueling
 * @property totalCost total cost of this refueling
 * @property pricePerLiter fuel price per liter at this refueling
 */
data class CostGraphDataPoint(
    val date: LocalDateTime,
    val totalCost: Double,
    val pricePerLiter: Double
)

/**
 * Response DTO for cost analysis graph data.
 *
 * @property vehicleId ID of the vehicle
 * @property dataPoints list of cost data points
 * @property totalCost total cost across all data points
 * @property averagePricePerLiter weighted average price per liter
 */
data class CostGraphResponse(
    val vehicleId: Long,
    val dataPoints: List<CostGraphDataPoint>,
    val totalCost: Double,
    val averagePricePerLiter: Double
)
