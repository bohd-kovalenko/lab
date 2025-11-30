package com.quaterion.service

import com.quaterion.dto.*
import com.quaterion.entity.User
import com.quaterion.entity.Vehicle
import com.quaterion.repository.UserRepository
import com.quaterion.repository.VehicleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Service for managing vehicle operations.
 *
 * Handles business logic for creating, reading, updating, and deleting vehicles.
 * All operations enforce user ownership validation.
 *
 * @property vehicleRepository repository for vehicle data access
 * @property userRepository repository for user data access
 */
@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository
) {

    /**
     * Creates a new vehicle for the specified user.
     *
     * Validates that the license plate is unique if provided.
     *
     * @param request vehicle creation data
     * @param userId ID of the user who will own the vehicle
     * @return [VehicleResponse] representing the created vehicle
     * @throws IllegalArgumentException if user not found or license plate already exists
     */
    @Transactional
    fun createVehicle(request: CreateVehicleRequest, userId: Long): VehicleResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        if (request.licensePlate != null && vehicleRepository.existsByLicensePlate(request.licensePlate)) {
            throw IllegalArgumentException("Vehicle with this license plate already exists")
        }

        val vehicle = Vehicle(
            user = user,
            make = request.make,
            model = request.model,
            year = request.year,
            licensePlate = request.licensePlate,
            fuelType = request.fuelType.uppercase(),
            tankCapacity = request.tankCapacity
        )

        val savedVehicle = vehicleRepository.save(vehicle)
        return toVehicleResponse(savedVehicle)
    }

    /**
     * Retrieves all vehicles belonging to a user.
     *
     * @param userId ID of the user whose vehicles to retrieve
     * @return list of [VehicleResponse] for all user's vehicles
     */
    @Transactional(readOnly = true)
    fun getAllVehicles(userId: Long): List<VehicleResponse> {
        return vehicleRepository.findByUserId(userId)
            .map { toVehicleResponse(it) }
    }

    /**
     * Retrieves a specific vehicle by ID with ownership verification.
     *
     * @param vehicleId ID of the vehicle to retrieve
     * @param userId ID of the user requesting the vehicle
     * @return [VehicleResponse] for the requested vehicle
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @Transactional(readOnly = true)
    fun getVehicleById(vehicleId: Long, userId: Long): VehicleResponse {
        val vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")
        return toVehicleResponse(vehicle)
    }

    /**
     * Updates an existing vehicle with partial update support.
     *
     * Only non-null fields in the request will be updated.
     * Validates license plate uniqueness if changed.
     *
     * @param vehicleId ID of the vehicle to update
     * @param request update data with optional fields
     * @param userId ID of the user requesting the update
     * @return [VehicleResponse] for the updated vehicle
     * @throws IllegalArgumentException if vehicle not found, user lacks access, or license plate already exists
     */
    @Transactional
    fun updateVehicle(vehicleId: Long, request: UpdateVehicleRequest, userId: Long): VehicleResponse {
        val existingVehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")

        if (request.licensePlate != null &&
            request.licensePlate != existingVehicle.licensePlate &&
            vehicleRepository.existsByLicensePlate(request.licensePlate)) {
            throw IllegalArgumentException("Vehicle with this license plate already exists")
        }

        val updatedVehicle = existingVehicle.copy(
            make = request.make ?: existingVehicle.make,
            model = request.model ?: existingVehicle.model,
            year = request.year ?: existingVehicle.year,
            licensePlate = request.licensePlate ?: existingVehicle.licensePlate,
            fuelType = request.fuelType?.uppercase() ?: existingVehicle.fuelType,
            tankCapacity = request.tankCapacity ?: existingVehicle.tankCapacity,
            updatedAt = LocalDateTime.now()
        )

        val savedVehicle = vehicleRepository.save(updatedVehicle)
        return toVehicleResponse(savedVehicle)
    }

    /**
     * Deletes a vehicle with ownership verification.
     *
     * @param vehicleId ID of the vehicle to delete
     * @param userId ID of the user requesting deletion
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @Transactional
    fun deleteVehicle(vehicleId: Long, userId: Long) {
        val vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")
        vehicleRepository.delete(vehicle)
    }

    /**
     * Converts a Vehicle entity to VehicleResponse DTO.
     *
     * @param vehicle entity to convert
     * @return [VehicleResponse] DTO representation
     */
    private fun toVehicleResponse(vehicle: Vehicle): VehicleResponse {
        return VehicleResponse(
            id = vehicle.id!!,
            make = vehicle.make,
            model = vehicle.model,
            year = vehicle.year,
            licensePlate = vehicle.licensePlate,
            fuelType = vehicle.fuelType,
            tankCapacity = vehicle.tankCapacity,
            createdAt = vehicle.createdAt,
            updatedAt = vehicle.updatedAt
        )
    }
}
