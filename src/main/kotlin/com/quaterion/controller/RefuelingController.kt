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

@RestController
@RequestMapping("/api/refuelings")
class RefuelingController(
    private val refuelingService: RefuelingService
) {

    @PostMapping
    fun createRefueling(
        @Valid @RequestBody request: CreateRefuelingRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<RefuelingResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val response = refuelingService.createRefueling(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/vehicle/{vehicleId}")
    fun getAllRefuelings(
        @PathVariable vehicleId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<List<RefuelingResponse>> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val refuelings = refuelingService.getAllRefuelings(vehicleId, userId)
        return ResponseEntity.ok(refuelings)
    }

    @GetMapping("/{id}")
    fun getRefuelingById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<RefuelingResponse> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        val refueling = refuelingService.getRefuelingById(id, userId)
        return ResponseEntity.ok(refueling)
    }

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

    @DeleteMapping("/{id}")
    fun deleteRefueling(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        val userId = userDetails.getUserId() ?: throw IllegalStateException("User ID not found")
        refuelingService.deleteRefueling(id, userId)
        return ResponseEntity.noContent().build()
    }

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
