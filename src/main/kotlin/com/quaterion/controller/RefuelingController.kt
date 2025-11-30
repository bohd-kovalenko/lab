package com.quaterion.controller

import com.quaterion.dto.*
import com.quaterion.security.CustomUserDetails
import com.quaterion.service.RefuelingService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * REST controller for refueling record management and fuel analytics.
 *
 * Provides CRUD endpoints for refueling records and analytical endpoints
 * for fuel statistics, consumption graphs, and cost analysis.
 *
 * @property refuelingService service for refueling business logic
 */
@RestController
@RequestMapping("/api/refuelings")
class RefuelingController(
    private val refuelingService: RefuelingService
) {

    /**
     * Creates a new refueling record.
     *
     * @param request refueling creation data including vehicle ID, date, odometer, fuel amount, and price
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with created [RefuelingResponse] (HTTP 201)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @PostMapping
    fun createRefueling(
        @Valid @RequestBody request: CreateRefuelingRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<RefuelingResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val response = refuelingService.createRefueling(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Retrieves all refueling records for a specific vehicle.
     *
     * @param vehicleId vehicle ID to get refuelings for
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with list of [RefuelingResponse] ordered by date descending (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @GetMapping("/vehicle/{vehicleId}")
    fun getAllRefuelings(
        @PathVariable vehicleId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<List<RefuelingResponse>> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val refuelings = refuelingService.getAllRefuelings(vehicleId, userId)
        return ResponseEntity.ok(refuelings)
    }

    /**
     * Retrieves a specific refueling record by ID.
     *
     * @param id refueling record ID to retrieve
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with [RefuelingResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if refueling not found or user lacks access
     */
    @GetMapping("/{id}")
    fun getRefuelingById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<RefuelingResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val refueling = refuelingService.getRefuelingById(id, userId)
        return ResponseEntity.ok(refueling)
    }

    /**
     * Updates an existing refueling record.
     *
     * Supports partial updates - only provided fields will be modified.
     * Total cost is automatically recalculated if fuel amount or price changes.
     *
     * @param id refueling record ID to update
     * @param request update data with optional fields
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with updated [RefuelingResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if refueling not found or user lacks access
     */
    @PutMapping("/{id}")
    fun updateRefueling(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateRefuelingRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<RefuelingResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val response = refuelingService.updateRefueling(id, request, userId)
        return ResponseEntity.ok(response)
    }

    /**
     * Deletes a refueling record.
     *
     * @param id refueling record ID to delete
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with no content (HTTP 204)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if refueling not found or user lacks access
     */
    @DeleteMapping("/{id}")
    fun deleteRefueling(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        refuelingService.deleteRefueling(id, userId)
        return ResponseEntity.noContent().build()
    }

    /**
     * Retrieves fuel statistics for a vehicle.
     *
     * Calculates total refuelings, fuel amount, cost, average consumption (L/100km),
     * average price per liter, and total distance traveled.
     *
     * @param vehicleId vehicle ID to get statistics for
     * @param startDate optional start date filter (ISO 8601 format)
     * @param endDate optional end date filter (ISO 8601 format)
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with [FuelStatisticsResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @GetMapping("/vehicle/{vehicleId}/statistics")
    fun getStatistics(
        @PathVariable vehicleId: Long,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<FuelStatisticsResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val statistics = refuelingService.getStatistics(vehicleId, startDate, endDate, userId)
        return ResponseEntity.ok(statistics)
    }

    /**
     * Retrieves fuel consumption graph data for a vehicle.
     *
     * Returns data points with consumption in L/100km calculated between
     * consecutive full-tank refuelings.
     *
     * @param vehicleId vehicle ID to get consumption data for
     * @param startDate optional start date filter (ISO 8601 format)
     * @param endDate optional end date filter (ISO 8601 format)
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with [ConsumptionGraphResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @GetMapping("/vehicle/{vehicleId}/consumption-graph")
    fun getConsumptionGraph(
        @PathVariable vehicleId: Long,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<ConsumptionGraphResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val graph = refuelingService.getConsumptionGraph(vehicleId, startDate, endDate, userId)
        return ResponseEntity.ok(graph)
    }

    /**
     * Retrieves cost analysis graph data for a vehicle.
     *
     * Returns data points with total cost and price per liter for each refueling,
     * along with overall totals and averages.
     *
     * @param vehicleId vehicle ID to get cost data for
     * @param startDate optional start date filter (ISO 8601 format)
     * @param endDate optional end date filter (ISO 8601 format)
     * @param userDetails authenticated user's details
     * @return [ResponseEntity] with [CostGraphResponse] (HTTP 200)
     * @throws IllegalStateException if user ID is not found in authentication
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @GetMapping("/vehicle/{vehicleId}/cost-graph")
    fun getCostGraph(
        @PathVariable vehicleId: Long,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<CostGraphResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val graph = refuelingService.getCostGraph(vehicleId, startDate, endDate, userId)
        return ResponseEntity.ok(graph)
    }
}
