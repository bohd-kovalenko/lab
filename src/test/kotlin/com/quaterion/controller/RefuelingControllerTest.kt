package com.quaterion.controller

import com.quaterion.dto.*
import com.quaterion.security.CustomUserDetails
import com.quaterion.service.RefuelingService
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class RefuelingControllerTest {

    private lateinit var refuelingService: RefuelingService
    private lateinit var refuelingController: RefuelingController
    private lateinit var userDetails: CustomUserDetails

    @BeforeEach
    fun setup() {
        refuelingService = mockk()
        refuelingController = RefuelingController(refuelingService)
        userDetails = mockk()

        every { userDetails.getUserId() } returns 1L
    }

    @Test
    fun `should create refueling successfully`() {
        val now = LocalDateTime.now()
        val request = CreateRefuelingRequest(
            vehicleId = 1L,
            date = now,
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            notes = "Test refueling",
            fullTank = true
        )

        val response = RefuelingResponse(
            id = 1L,
            vehicleId = 1L,
            date = now,
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            totalCost = 68.25,
            notes = "Test refueling",
            fullTank = true,
            fuelConsumption = null,
            distanceSinceLastRefueling = null,
            createdAt = now,
            updatedAt = now
        )

        every { refuelingService.createRefueling(request, 1L) } returns response

        val result = refuelingController.createRefueling(request, userDetails)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(response, result.body)

        verify { refuelingService.createRefueling(request, 1L) }
        verify { userDetails.getUserId() }
    }

    @Test
    fun `should get all refuelings for vehicle`() {
        val now = LocalDateTime.now()
        val refuelings = listOf(
            RefuelingResponse(
                id = 1L,
                vehicleId = 1L,
                date = now.minusDays(1),
                odometer = 10000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                notes = null,
                fullTank = true,
                fuelConsumption = null,
                distanceSinceLastRefueling = null,
                createdAt = now,
                updatedAt = now
            ),
            RefuelingResponse(
                id = 2L,
                vehicleId = 1L,
                date = now,
                odometer = 10500.0,
                fuelAmount = 45.0,
                pricePerLiter = 1.6,
                totalCost = 72.0,
                notes = null,
                fullTank = true,
                fuelConsumption = 9.0,
                distanceSinceLastRefueling = 500.0,
                createdAt = now,
                updatedAt = now
            )
        )

        every { refuelingService.getAllRefuelings(1L, 1L) } returns refuelings

        val result = refuelingController.getAllRefuelings(1L, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(2, result.body?.size)
        assertEquals(refuelings, result.body)

        verify { refuelingService.getAllRefuelings(1L, 1L) }
    }

    @Test
    fun `should get refueling by id`() {
        val now = LocalDateTime.now()
        val refueling = RefuelingResponse(
            id = 1L,
            vehicleId = 1L,
            date = now,
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            notes = "Test",
            fullTank = true,
            fuelConsumption = null,
            distanceSinceLastRefueling = null,
            createdAt = now,
            updatedAt = now
        )

        every { refuelingService.getRefuelingById(1L, 1L) } returns refueling

        val result = refuelingController.getRefuelingById(1L, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(refueling, result.body)

        verify { refuelingService.getRefuelingById(1L, 1L) }
    }

    @Test
    fun `should update refueling`() {
        val now = LocalDateTime.now()
        val request = UpdateRefuelingRequest(
            fuelAmount = 50.0,
            fullTank = true
        )

        val response = RefuelingResponse(
            id = 1L,
            vehicleId = 1L,
            date = now,
            odometer = 10000.0,
            fuelAmount = 50.0,
            pricePerLiter = 1.5,
            totalCost = 75.0,
            notes = null,
            fullTank = true,
            fuelConsumption = null,
            distanceSinceLastRefueling = null,
            createdAt = now,
            updatedAt = now
        )

        every { refuelingService.updateRefueling(1L, request, 1L) } returns response

        val result = refuelingController.updateRefueling(1L, request, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response, result.body)

        verify { refuelingService.updateRefueling(1L, request, 1L) }
    }

    @Test
    fun `should delete refueling`() {
        every { refuelingService.deleteRefueling(1L, 1L) } just Runs

        val result = refuelingController.deleteRefueling(1L, userDetails)

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
        assertNull(result.body)

        verify { refuelingService.deleteRefueling(1L, 1L) }
    }

    @Test
    fun `should get statistics`() {
        val now = LocalDateTime.now()
        val stats = FuelStatisticsResponse(
            vehicleId = 1L,
            totalRefuelings = 3,
            totalFuelAmount = 127.0,
            totalCost = 197.1,
            averageConsumption = 8.7,
            averagePricePerLiter = 1.55,
            totalDistance = 1000.0,
            periodStart = now.minusDays(2),
            periodEnd = now
        )

        every { refuelingService.getStatistics(1L, null, null, 1L) } returns stats

        val result = refuelingController.getStatistics(1L, null, null, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(stats, result.body)

        verify { refuelingService.getStatistics(1L, null, null, 1L) }
    }

    @Test
    fun `should get statistics with date range`() {
        val now = LocalDateTime.now()
        val startDate = now.minusDays(7)
        val endDate = now

        val stats = FuelStatisticsResponse(
            vehicleId = 1L,
            totalRefuelings = 2,
            totalFuelAmount = 85.0,
            totalCost = 131.0,
            averageConsumption = 8.5,
            averagePricePerLiter = 1.54,
            totalDistance = 500.0,
            periodStart = startDate,
            periodEnd = endDate
        )

        every { refuelingService.getStatistics(1L, startDate, endDate, 1L) } returns stats

        val result = refuelingController.getStatistics(1L, startDate, endDate, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(stats, result.body)

        verify { refuelingService.getStatistics(1L, startDate, endDate, 1L) }
    }

    @Test
    fun `should get consumption graph`() {
        val now = LocalDateTime.now()
        val graph = ConsumptionGraphResponse(
            vehicleId = 1L,
            dataPoints = listOf(
                ConsumptionGraphDataPoint(now.minusDays(1), 9.0, 10500.0),
                ConsumptionGraphDataPoint(now, 8.4, 11000.0)
            ),
            averageConsumption = 8.7
        )

        every { refuelingService.getConsumptionGraph(1L, null, null, 1L) } returns graph

        val result = refuelingController.getConsumptionGraph(1L, null, null, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(graph, result.body)

        verify { refuelingService.getConsumptionGraph(1L, null, null, 1L) }
    }

    @Test
    fun `should get cost graph`() {
        val now = LocalDateTime.now()
        val graph = CostGraphResponse(
            vehicleId = 1L,
            dataPoints = listOf(
                CostGraphDataPoint(now.minusDays(1), 60.0, 1.5),
                CostGraphDataPoint(now, 72.0, 1.6)
            ),
            totalCost = 132.0,
            averagePricePerLiter = 1.55
        )

        every { refuelingService.getCostGraph(1L, null, null, 1L) } returns graph

        val result = refuelingController.getCostGraph(1L, null, null, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(graph, result.body)

        verify { refuelingService.getCostGraph(1L, null, null, 1L) }
    }

    @Test
    fun `should throw exception when user id is null`() {
        every { userDetails.getUserId() } returns null

        val request = CreateRefuelingRequest(
            vehicleId = 1L,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            fullTank = true
        )

        assertThrows(IllegalStateException::class.java) {
            refuelingController.createRefueling(request, userDetails)
        }

        verify(exactly = 0) { refuelingService.createRefueling(any(), any()) }
    }

    @Test
    fun `should handle service exception when creating refueling`() {
        val request = CreateRefuelingRequest(
            vehicleId = 1L,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            fullTank = true
        )

        every { refuelingService.createRefueling(request, 1L) } throws IllegalArgumentException("Vehicle not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            refuelingController.createRefueling(request, userDetails)
        }

        verify { refuelingService.createRefueling(request, 1L) }
    }

    @Test
    fun `should handle service exception when getting refueling`() {
        every { refuelingService.getRefuelingById(1L, 1L) } throws IllegalArgumentException("Refueling record not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            refuelingController.getRefuelingById(1L, userDetails)
        }

        verify { refuelingService.getRefuelingById(1L, 1L) }
    }

    @Test
    fun `should handle service exception when updating refueling`() {
        val request = UpdateRefuelingRequest(fuelAmount = 50.0)

        every { refuelingService.updateRefueling(1L, request, 1L) } throws IllegalArgumentException("Refueling record not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            refuelingController.updateRefueling(1L, request, userDetails)
        }

        verify { refuelingService.updateRefueling(1L, request, 1L) }
    }

    @Test
    fun `should handle service exception when deleting refueling`() {
        every { refuelingService.deleteRefueling(1L, 1L) } throws IllegalArgumentException("Refueling record not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            refuelingController.deleteRefueling(1L, userDetails)
        }

        verify { refuelingService.deleteRefueling(1L, 1L) }
    }

    @Test
    fun `should handle service exception when getting statistics`() {
        every { refuelingService.getStatistics(1L, null, null, 1L) } throws IllegalArgumentException("Vehicle not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            refuelingController.getStatistics(1L, null, null, userDetails)
        }

        verify { refuelingService.getStatistics(1L, null, null, 1L) }
    }
}
