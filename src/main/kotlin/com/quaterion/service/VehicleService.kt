package com.quaterion.service

import com.quaterion.dto.*
import com.quaterion.entity.User
import com.quaterion.entity.Vehicle
import com.quaterion.repository.UserRepository
import com.quaterion.repository.VehicleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository
) {

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

    @Transactional(readOnly = true)
    fun getAllVehicles(userId: Long): List<VehicleResponse> {
        return vehicleRepository.findByUserId(userId)
            .map { toVehicleResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getVehicleById(vehicleId: Long, userId: Long): VehicleResponse {
        val vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")
        return toVehicleResponse(vehicle)
    }

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

    @Transactional
    fun deleteVehicle(vehicleId: Long, userId: Long) {
        val vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")
        vehicleRepository.delete(vehicle)
    }

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
