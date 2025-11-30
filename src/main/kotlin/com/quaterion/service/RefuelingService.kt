package com.quaterion.service

import com.quaterion.dto.*
import com.quaterion.entity.Refueling
import com.quaterion.repository.RefuelingRepository
import com.quaterion.repository.VehicleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Service for managing refueling operations and fuel analytics.
 *
 * Handles business logic for CRUD operations on refueling records
 * and provides analytical functions for fuel statistics and consumption tracking.
 *
 * @property refuelingRepository repository for refueling data access
 * @property vehicleRepository repository for vehicle data access
 */
@Service
class RefuelingService(
    private val refuelingRepository: RefuelingRepository,
    private val vehicleRepository: VehicleRepository
) {

    /**
     * Creates a new refueling record for a vehicle.
     *
     * Automatically calculates total cost based on fuel amount and price per liter.
     *
     * @param request refueling creation data
     * @param userId ID of the user creating the record
     * @return [RefuelingResponse] representing the created refueling record
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @Transactional
    fun createRefueling(request: CreateRefuelingRequest, userId: Long): RefuelingResponse {
        val vehicle = vehicleRepository.findByIdAndUserId(request.vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")

        val totalCost = request.fuelAmount * request.pricePerLiter

        val refueling = Refueling(
            vehicle = vehicle,
            date = request.date,
            odometer = request.odometer,
            fuelAmount = request.fuelAmount,
            pricePerLiter = request.pricePerLiter,
            totalCost = totalCost,
            notes = request.notes,
            fullTank = request.fullTank
        )

        val savedRefueling = refuelingRepository.save(refueling)
        return toRefuelingResponse(savedRefueling, userId)
    }

    /**
     * Retrieves all refueling records for a specific vehicle.
     *
     * @param vehicleId ID of the vehicle to get refuelings for
     * @param userId ID of the user requesting the records
     * @return list of [RefuelingResponse] ordered by date descending
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @Transactional(readOnly = true)
    fun getAllRefuelings(vehicleId: Long, userId: Long): List<RefuelingResponse> {
        vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")

        return refuelingRepository.findByVehicleIdAndUserId(vehicleId, userId)
            .map { toRefuelingResponse(it, userId) }
    }

    /**
     * Retrieves a specific refueling record by ID.
     *
     * @param refuelingId ID of the refueling record to retrieve
     * @param userId ID of the user requesting the record
     * @return [RefuelingResponse] for the requested record
     * @throws IllegalArgumentException if refueling not found or user lacks access
     */
    @Transactional(readOnly = true)
    fun getRefuelingById(refuelingId: Long, userId: Long): RefuelingResponse {
        val refueling = refuelingRepository.findByIdAndUserId(refuelingId, userId)
            ?: throw IllegalArgumentException("Refueling record not found or access denied")
        return toRefuelingResponse(refueling, userId)
    }

    /**
     * Updates an existing refueling record with partial update support.
     *
     * Only non-null fields in the request will be updated.
     * Total cost is automatically recalculated if fuel amount or price changes.
     *
     * @param refuelingId ID of the refueling record to update
     * @param request update data with optional fields
     * @param userId ID of the user requesting the update
     * @return [RefuelingResponse] for the updated record
     * @throws IllegalArgumentException if refueling not found or user lacks access
     */
    @Transactional
    fun updateRefueling(refuelingId: Long, request: UpdateRefuelingRequest, userId: Long): RefuelingResponse {
        val existingRefueling = refuelingRepository.findByIdAndUserId(refuelingId, userId)
            ?: throw IllegalArgumentException("Refueling record not found or access denied")

        val updatedFuelAmount = request.fuelAmount ?: existingRefueling.fuelAmount
        val updatedPricePerLiter = request.pricePerLiter ?: existingRefueling.pricePerLiter
        val updatedTotalCost = updatedFuelAmount * updatedPricePerLiter

        val updatedRefueling = existingRefueling.copy(
            date = request.date ?: existingRefueling.date,
            odometer = request.odometer ?: existingRefueling.odometer,
            fuelAmount = updatedFuelAmount,
            pricePerLiter = updatedPricePerLiter,
            totalCost = updatedTotalCost,
            notes = request.notes ?: existingRefueling.notes,
            fullTank = request.fullTank ?: existingRefueling.fullTank,
            updatedAt = LocalDateTime.now()
        )

        val savedRefueling = refuelingRepository.save(updatedRefueling)
        return toRefuelingResponse(savedRefueling, userId)
    }

    /**
     * Deletes a refueling record with ownership verification.
     *
     * @param refuelingId ID of the refueling record to delete
     * @param userId ID of the user requesting deletion
     * @throws IllegalArgumentException if refueling not found or user lacks access
     */
    @Transactional
    fun deleteRefueling(refuelingId: Long, userId: Long) {
        val refueling = refuelingRepository.findByIdAndUserId(refuelingId, userId)
            ?: throw IllegalArgumentException("Refueling record not found or access denied")
        refuelingRepository.delete(refueling)
    }

    /**
     * Calculates comprehensive fuel statistics for a vehicle.
     *
     * Computes total refuelings, total fuel amount, total cost, average consumption (L/100km),
     * average price per liter, and total distance traveled. Optionally filters by date range.
     *
     * @param vehicleId ID of the vehicle to get statistics for
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @param userId ID of the user requesting statistics
     * @return [FuelStatisticsResponse] containing calculated statistics
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @Transactional(readOnly = true)
    fun getStatistics(vehicleId: Long, startDate: LocalDateTime?, endDate: LocalDateTime?, userId: Long): FuelStatisticsResponse {
        vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")

        val refuelings = if (startDate != null && endDate != null) {
            refuelingRepository.findByVehicleIdAndDateBetweenOrderByDateAsc(vehicleId, startDate, endDate)
        } else {
            refuelingRepository.findByVehicleIdOrderByDateDesc(vehicleId)
        }

        if (refuelings.isEmpty()) {
            return FuelStatisticsResponse(
                vehicleId = vehicleId,
                totalRefuelings = 0,
                totalFuelAmount = 0.0,
                totalCost = 0.0,
                averageConsumption = null,
                averagePricePerLiter = 0.0,
                totalDistance = 0.0,
                periodStart = null,
                periodEnd = null
            )
        }

        val sortedRefuelings = refuelings.sortedBy { it.odometer }
        val totalFuelAmount = refuelings.sumOf { it.fuelAmount }
        val totalCost = refuelings.sumOf { it.totalCost }
        val averagePricePerLiter = totalCost / totalFuelAmount
        val totalDistance = sortedRefuelings.last().odometer - sortedRefuelings.first().odometer

        val averageConsumption = if (sortedRefuelings.size >= 2 && totalDistance > 0) {
            val fuelForConsumption = sortedRefuelings.drop(1).sumOf { it.fuelAmount }
            (fuelForConsumption / totalDistance) * 100
        } else {
            null
        }

        return FuelStatisticsResponse(
            vehicleId = vehicleId,
            totalRefuelings = refuelings.size,
            totalFuelAmount = totalFuelAmount,
            totalCost = totalCost,
            averageConsumption = averageConsumption,
            averagePricePerLiter = averagePricePerLiter,
            totalDistance = totalDistance,
            periodStart = sortedRefuelings.first().date,
            periodEnd = sortedRefuelings.last().date
        )
    }

    /**
     * Generates fuel consumption graph data for a vehicle.
     *
     * Calculates consumption in L/100km between consecutive full-tank refuelings.
     * Consumption can only be calculated when the current refueling was a full tank fill.
     *
     * @param vehicleId ID of the vehicle to get consumption data for
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @param userId ID of the user requesting the data
     * @return [ConsumptionGraphResponse] containing data points and average consumption
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @Transactional(readOnly = true)
    fun getConsumptionGraph(vehicleId: Long, startDate: LocalDateTime?, endDate: LocalDateTime?, userId: Long): ConsumptionGraphResponse {
        vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")

        val refuelings = if (startDate != null && endDate != null) {
            refuelingRepository.findByVehicleIdAndDateBetweenOrderByDateAsc(vehicleId, startDate, endDate)
        } else {
            refuelingRepository.findByVehicleIdOrderByDateDesc(vehicleId).sortedBy { it.date }
        }

        val dataPoints = mutableListOf<ConsumptionGraphDataPoint>()
        val consumptions = mutableListOf<Double>()

        for (i in 1 until refuelings.size) {
            val current = refuelings[i]
            val previous = refuelings[i - 1]

            val distance = current.odometer - previous.odometer
            if (distance > 0 && current.fullTank) {
                val consumption = (current.fuelAmount / distance) * 100
                consumptions.add(consumption)
                dataPoints.add(
                    ConsumptionGraphDataPoint(
                        date = current.date,
                        fuelConsumption = consumption,
                        odometer = current.odometer
                    )
                )
            }
        }

        val averageConsumption = if (consumptions.isNotEmpty()) {
            consumptions.average()
        } else {
            null
        }

        return ConsumptionGraphResponse(
            vehicleId = vehicleId,
            dataPoints = dataPoints,
            averageConsumption = averageConsumption
        )
    }

    /**
     * Generates cost analysis graph data for a vehicle.
     *
     * Returns data points with total cost and price per liter for each refueling,
     * along with overall totals and weighted average price per liter.
     *
     * @param vehicleId ID of the vehicle to get cost data for
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @param userId ID of the user requesting the data
     * @return [CostGraphResponse] containing data points and aggregated cost data
     * @throws IllegalArgumentException if vehicle not found or user lacks access
     */
    @Transactional(readOnly = true)
    fun getCostGraph(vehicleId: Long, startDate: LocalDateTime?, endDate: LocalDateTime?, userId: Long): CostGraphResponse {
        vehicleRepository.findByIdAndUserId(vehicleId, userId)
            ?: throw IllegalArgumentException("Vehicle not found or access denied")

        val refuelings = if (startDate != null && endDate != null) {
            refuelingRepository.findByVehicleIdAndDateBetweenOrderByDateAsc(vehicleId, startDate, endDate)
        } else {
            refuelingRepository.findByVehicleIdOrderByDateDesc(vehicleId).sortedBy { it.date }
        }

        val dataPoints = refuelings.map { refueling ->
            CostGraphDataPoint(
                date = refueling.date,
                totalCost = refueling.totalCost,
                pricePerLiter = refueling.pricePerLiter
            )
        }

        val totalCost = refuelings.sumOf { it.totalCost }
        val totalFuel = refuelings.sumOf { it.fuelAmount }
        val averagePricePerLiter = if (totalFuel > 0) totalCost / totalFuel else 0.0

        return CostGraphResponse(
            vehicleId = vehicleId,
            dataPoints = dataPoints,
            totalCost = totalCost,
            averagePricePerLiter = averagePricePerLiter
        )
    }

    /**
     * Converts a Refueling entity to RefuelingResponse DTO.
     *
     * Calculates fuel consumption and distance since last refueling based on
     * the vehicle's refueling history.
     *
     * @param refueling entity to convert
     * @param userId ID of the user for authorization check
     * @return [RefuelingResponse] DTO representation with calculated consumption data
     */
    private fun toRefuelingResponse(refueling: Refueling, userId: Long): RefuelingResponse {
        val allRefuelings = refuelingRepository.findByVehicleIdAndUserId(refueling.vehicle.id!!, userId)
            .sortedBy { it.odometer }

        val currentIndex = allRefuelings.indexOfFirst { it.id == refueling.id }

        val (fuelConsumption, distanceSinceLastRefueling) = if (currentIndex > 0) {
            val previous = allRefuelings[currentIndex - 1]
            val distance = refueling.odometer - previous.odometer
            val consumption = if (distance > 0 && refueling.fullTank) {
                (refueling.fuelAmount / distance) * 100
            } else {
                null
            }
            Pair(consumption, distance)
        } else {
            Pair(null, null)
        }

        return RefuelingResponse(
            id = refueling.id!!,
            vehicleId = refueling.vehicle.id!!,
            date = refueling.date,
            odometer = refueling.odometer,
            fuelAmount = refueling.fuelAmount,
            pricePerLiter = refueling.pricePerLiter,
            totalCost = refueling.totalCost,
            notes = refueling.notes,
            fullTank = refueling.fullTank,
            fuelConsumption = fuelConsumption,
            distanceSinceLastRefueling = distanceSinceLastRefueling,
            createdAt = refueling.createdAt,
            updatedAt = refueling.updatedAt
        )
    }
}
