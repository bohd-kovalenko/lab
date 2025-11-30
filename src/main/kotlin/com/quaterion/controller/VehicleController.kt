package com.quaterion.controller

import com.quaterion.dto.CreateVehicleRequest
import com.quaterion.dto.UpdateVehicleRequest
import com.quaterion.dto.VehicleResponse
import com.quaterion.security.CustomUserDetails
import com.quaterion.service.VehicleService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/**
 * REST controller for vehicle management operations.
 *
 * Provides CRUD endpoints for managing user vehicles. All operations
 * are scoped to the authenticated user's vehicles only.
 *
 * @property vehicleService service for vehicle business logic
 */
@RestController
@RequestMapping("/api/vehicles")
class VehicleController(
    private val vehicleService: VehicleService
) {

    /**
     * Creates a new vehicle for the authenticated user.
     *
     * @param request vehicle creation data
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with created [VehicleResponse] (HTTP 201)
     * @throws IllegalStateException if user ID is not found in authentication
     */
    @PostMapping
    fun createVehicle(
        @Valid @RequestBody request: CreateVehicleRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<VehicleResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val response = vehicleService.createVehicle(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Retrieves all vehicles belonging to the authenticated user.
     *
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with list of [VehicleResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     */
    @GetMapping
    fun getAllVehicles(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<List<VehicleResponse>> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val vehicles = vehicleService.getAllVehicles(userId)
        return ResponseEntity.ok(vehicles)
    }

    /**
     * Retrieves a specific vehicle by ID.
     *
     * @param id vehicle ID to retrieve
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with [VehicleResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @GetMapping("/{id}")
    fun getVehicleById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<VehicleResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val vehicle = vehicleService.getVehicleById(id, userId)
        return ResponseEntity.ok(vehicle)
    }

    /**
     * Updates an existing vehicle.
     *
     * Supports partial updates - only provided fields will be modified.
     *
     * @param id vehicle ID to update
     * @param request update data with optional fields
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with updated [VehicleResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @PutMapping("/{id}")
    fun updateVehicle(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateVehicleRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<VehicleResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val response = vehicleService.updateVehicle(id, request, userId)
        return ResponseEntity.ok(response)
    }

    /**
     * Deletes a vehicle.
     *
     * @param id vehicle ID to delete
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with no content (HTTP 204)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @DeleteMapping("/{id}")
    fun deleteVehicle(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        vehicleService.deleteVehicle(id, userId)
        return ResponseEntity.noContent().build()
    }
}
