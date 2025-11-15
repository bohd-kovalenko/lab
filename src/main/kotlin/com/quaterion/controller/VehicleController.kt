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

@RestController
@RequestMapping("/api/vehicles")
class VehicleController(
    private val vehicleService: VehicleService
) {

    @PostMapping
    fun createVehicle(
        @Valid @RequestBody request: CreateVehicleRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<VehicleResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val response = vehicleService.createVehicle(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllVehicles(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<List<VehicleResponse>> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val vehicles = vehicleService.getAllVehicles(userId)
        return ResponseEntity.ok(vehicles)
    }
    @GetMapping("/{id}")
    fun getVehicleById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<VehicleResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val vehicle = vehicleService.getVehicleById(id, userId)
        return ResponseEntity.ok(vehicle)
    }

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
