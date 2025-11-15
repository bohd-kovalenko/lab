package com.quaterion.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

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
