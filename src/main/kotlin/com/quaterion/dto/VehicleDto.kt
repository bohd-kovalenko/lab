package com.quaterion.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

/**
 * Request DTO for creating a new vehicle.
 *
 * @property make vehicle manufacturer (required)
 * @property model vehicle model name (required)
 * @property year manufacturing year (1900-2100)
 * @property licensePlate optional unique license plate number
 * @property fuelType fuel type (GASOLINE, DIESEL, ELECTRIC, HYBRID)
 * @property tankCapacity fuel tank capacity in liters (must be positive)
 */
data class CreateVehicleRequest(
    @field:NotBlank(message = "Make is required")
    val make: String,

    @field:NotBlank(message = "Model is required")
    val model: String,

    @field:Min(value = 1900, message = "Year must be 1900 or later")
    @field:Max(value = 2100, message = "Year must be 2100 or earlier")
    val year: Int,

    val licensePlate: String? = null,

    @field:NotBlank(message = "Fuel type is required")
    val fuelType: String,

    @field:Positive(message = "Tank capacity must be positive")
    val tankCapacity: Double
)

/**
 * Request DTO for updating an existing vehicle.
 *
 * All fields are optional - only provided fields will be updated.
 *
 * @property make vehicle manufacturer
 * @property model vehicle model name
 * @property year manufacturing year (1900-2100)
 * @property licensePlate license plate number
 * @property fuelType fuel type
 * @property tankCapacity fuel tank capacity in liters
 */
data class UpdateVehicleRequest(
    val make: String? = null,
    val model: String? = null,
    @field:Min(value = 1900, message = "Year must be 1900 or later")
    @field:Max(value = 2100, message = "Year must be 2100 or earlier")
    val year: Int? = null,
    val licensePlate: String? = null,
    val fuelType: String? = null,
    @field:Positive(message = "Tank capacity must be positive")
    val tankCapacity: Double? = null
)

/**
 * Response DTO for vehicle data.
 *
 * @property id unique identifier
 * @property make vehicle manufacturer
 * @property model vehicle model name
 * @property year manufacturing year
 * @property licensePlate license plate number (nullable)
 * @property fuelType type of fuel
 * @property tankCapacity fuel tank capacity in liters
 * @property createdAt timestamp when the vehicle was created
 * @property updatedAt timestamp of the last update
 */
data class VehicleResponse(
    val id: Long,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String?,
    val fuelType: String,
    val tankCapacity: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
