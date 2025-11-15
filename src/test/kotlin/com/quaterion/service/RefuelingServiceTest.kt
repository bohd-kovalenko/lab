package com.quaterion.service

import com.quaterion.dto.CreateRefuelingRequest
import com.quaterion.dto.UpdateRefuelingRequest
import com.quaterion.entity.Refueling
import com.quaterion.entity.User
import com.quaterion.entity.Vehicle
import com.quaterion.repository.RefuelingRepository
import com.quaterion.repository.VehicleRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class RefuelingServiceTest {

    private lateinit var refuelingRepository: RefuelingRepository
    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var refuelingService: RefuelingService

    private val testUser = User(id = 1L, username = "testuser", password = "password", role = "USER")
    private val testVehicle = Vehicle(
        id = 1L,
        user = testUser,
        make = "Toyota",
        model = "Camry",
        year = 2020,
        fuelType = "GASOLINE",
        tankCapacity = 60.0
    )

    @BeforeEach
    fun setup() {
        refuelingRepository = mockk()
        vehicleRepository = mockk()
        refuelingService = RefuelingService(refuelingRepository, vehicleRepository)
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

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns testVehicle
        every { refuelingRepository.save(any<Refueling>()) } answers {
            firstArg<Refueling>().copy(id = 1L)
        }
        every { refuelingRepository.findByVehicleIdAndUserId(1L, 1L) } returns emptyList()

        val response = refuelingService.createRefueling(request, 1L)

        assertEquals(1L, response.id)
        assertEquals(1L, response.vehicleId)
        assertEquals(10000.0, response.odometer)
        assertEquals(45.5, response.fuelAmount)
        assertEquals(1.5, response.pricePerLiter)
        assertEquals(68.25, response.totalCost)
        assertEquals("Test refueling", response.notes)
        assertTrue(response.fullTank)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
        verify { refuelingRepository.save(any<Refueling>()) }
    }

    @Test
    fun `should throw exception when vehicle not found`() {
        val request = CreateRefuelingRequest(
            vehicleId = 1L,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            fullTank = true
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            refuelingService.createRefueling(request, 1L)
        }

        assertEquals("Vehicle not found or access denied", exception.message)
        verify(exactly = 0) { refuelingRepository.save(any<Refueling>()) }
    }

    @Test
    fun `should get all refuelings for vehicle`() {
        val refueling1 = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now().minusDays(1),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        val refueling2 = Refueling(
            id = 2L,
            vehicle = testVehicle,
            date = LocalDateTime.now(),
            odometer = 10500.0,
            fuelAmount = 45.0,
            pricePerLiter = 1.6,
            totalCost = 72.0,
            fullTank = true
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns testVehicle
        every { refuelingRepository.findByVehicleIdAndUserId(1L, 1L) } returns listOf(refueling2, refueling1)

        val response = refuelingService.getAllRefuelings(1L, 1L)

        assertEquals(2, response.size)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
        verify { refuelingRepository.findByVehicleIdAndUserId(1L, 1L) }
    }

    @Test
    fun `should get refueling by id`() {
        val refueling = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        every { refuelingRepository.findByIdAndUserId(1L, 1L) } returns refueling
        every { refuelingRepository.findByVehicleIdAndUserId(1L, 1L) } returns listOf(refueling)

        val response = refuelingService.getRefuelingById(1L, 1L)

        assertEquals(1L, response.id)
        assertEquals(10000.0, response.odometer)

        verify { refuelingRepository.findByIdAndUserId(1L, 1L) }
    }

    @Test
    fun `should throw exception when getting non-existent refueling`() {
        every { refuelingRepository.findByIdAndUserId(1L, 1L) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            refuelingService.getRefuelingById(1L, 1L)
        }

        assertEquals("Refueling record not found or access denied", exception.message)
    }

    @Test
    fun `should update refueling successfully`() {
        val existingRefueling = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = false
        )

        val updateRequest = UpdateRefuelingRequest(
            fuelAmount = 50.0,
            fullTank = true
        )

        every { refuelingRepository.findByIdAndUserId(1L, 1L) } returns existingRefueling
        every { refuelingRepository.save(any<Refueling>()) } answers { firstArg() }
        every { refuelingRepository.findByVehicleIdAndUserId(1L, 1L) } returns listOf(existingRefueling)

        val response = refuelingService.updateRefueling(1L, updateRequest, 1L)

        assertEquals(50.0, response.fuelAmount)
        assertEquals(75.0, response.totalCost)
        assertTrue(response.fullTank)

        verify { refuelingRepository.save(any<Refueling>()) }
    }

    @Test
    fun `should delete refueling successfully`() {
        val refueling = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        every { refuelingRepository.findByIdAndUserId(1L, 1L) } returns refueling
        every { refuelingRepository.delete(refueling) } just Runs

        refuelingService.deleteRefueling(1L, 1L)

        verify { refuelingRepository.findByIdAndUserId(1L, 1L) }
        verify { refuelingRepository.delete(refueling) }
    }

    @Test
    fun `should calculate statistics correctly`() {
        val refueling1 = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now().minusDays(2),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        val refueling2 = Refueling(
            id = 2L,
            vehicle = testVehicle,
            date = LocalDateTime.now().minusDays(1),
            odometer = 10500.0,
            fuelAmount = 45.0,
            pricePerLiter = 1.6,
            totalCost = 72.0,
            fullTank = true
        )

        val refueling3 = Refueling(
            id = 3L,
            vehicle = testVehicle,
            date = LocalDateTime.now(),
            odometer = 11000.0,
            fuelAmount = 42.0,
            pricePerLiter = 1.55,
            totalCost = 65.1,
            fullTank = true
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns testVehicle
        every { refuelingRepository.findByVehicleIdOrderByDateDesc(1L) } returns listOf(refueling3, refueling2, refueling1)

        val stats = refuelingService.getStatistics(1L, null, null, 1L)

        assertEquals(3, stats.totalRefuelings)
        assertEquals(127.0, stats.totalFuelAmount)
        assertEquals(197.1, stats.totalCost, 0.01)
        assertEquals(1000.0, stats.totalDistance)
        assertNotNull(stats.averageConsumption)
        assertTrue(stats.averageConsumption!! > 0)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
        verify { refuelingRepository.findByVehicleIdOrderByDateDesc(1L) }
    }

    @Test
    fun `should return empty statistics when no refuelings exist`() {
        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns testVehicle
        every { refuelingRepository.findByVehicleIdOrderByDateDesc(1L) } returns emptyList()

        val stats = refuelingService.getStatistics(1L, null, null, 1L)

        assertEquals(0, stats.totalRefuelings)
        assertEquals(0.0, stats.totalFuelAmount)
        assertEquals(0.0, stats.totalCost)
        assertNull(stats.averageConsumption)

        verify { refuelingRepository.findByVehicleIdOrderByDateDesc(1L) }
    }

    @Test
    fun `should get consumption graph data`() {
        val refueling1 = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now().minusDays(2),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        val refueling2 = Refueling(
            id = 2L,
            vehicle = testVehicle,
            date = LocalDateTime.now().minusDays(1),
            odometer = 10500.0,
            fuelAmount = 45.0,
            pricePerLiter = 1.6,
            totalCost = 72.0,
            fullTank = true
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns testVehicle
        every { refuelingRepository.findByVehicleIdOrderByDateDesc(1L) } returns listOf(refueling2, refueling1)

        val graph = refuelingService.getConsumptionGraph(1L, null, null, 1L)

        assertEquals(1L, graph.vehicleId)
        assertEquals(1, graph.dataPoints.size)
        assertNotNull(graph.averageConsumption)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
        verify { refuelingRepository.findByVehicleIdOrderByDateDesc(1L) }
    }

    @Test
    fun `should get cost graph data`() {
        val refueling1 = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now().minusDays(1),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        val refueling2 = Refueling(
            id = 2L,
            vehicle = testVehicle,
            date = LocalDateTime.now(),
            odometer = 10500.0,
            fuelAmount = 45.0,
            pricePerLiter = 1.6,
            totalCost = 72.0,
            fullTank = true
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns testVehicle
        every { refuelingRepository.findByVehicleIdOrderByDateDesc(1L) } returns listOf(refueling2, refueling1)

        val graph = refuelingService.getCostGraph(1L, null, null, 1L)

        assertEquals(1L, graph.vehicleId)
        assertEquals(2, graph.dataPoints.size)
        assertEquals(132.0, graph.totalCost)
        assertTrue(graph.averagePricePerLiter > 0)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
    }

    @Test
    fun `should calculate fuel consumption between refuelings`() {
        val refueling1 = Refueling(
            id = 1L,
            vehicle = testVehicle,
            date = LocalDateTime.now().minusDays(1),
            odometer = 10000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        val refueling2 = Refueling(
            id = 2L,
            vehicle = testVehicle,
            date = LocalDateTime.now(),
            odometer = 10500.0,
            fuelAmount = 45.0,
            pricePerLiter = 1.6,
            totalCost = 72.0,
            fullTank = true
        )

        every { refuelingRepository.findByIdAndUserId(2L, 1L) } returns refueling2
        every { refuelingRepository.findByVehicleIdAndUserId(1L, 1L) } returns listOf(refueling1, refueling2)

        val response = refuelingService.getRefuelingById(2L, 1L)

        assertNotNull(response.fuelConsumption)
        assertEquals(500.0, response.distanceSinceLastRefueling)
        assertEquals(9.0, response.fuelConsumption!!, 0.01) // 45.0 / 500.0 * 100

        verify { refuelingRepository.findByIdAndUserId(2L, 1L) }
    }
}
