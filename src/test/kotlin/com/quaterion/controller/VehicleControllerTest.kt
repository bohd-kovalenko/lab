package com.quaterion.controller

import com.quaterion.dto.CreateVehicleRequest
import com.quaterion.dto.UpdateVehicleRequest
import com.quaterion.dto.VehicleResponse
import com.quaterion.security.CustomUserDetails
import com.quaterion.service.VehicleService
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class VehicleControllerTest {

    private lateinit var vehicleService: VehicleService
    private lateinit var vehicleController: VehicleController
    private lateinit var userDetails: CustomUserDetails

    @BeforeEach
    fun setup() {
        vehicleService = mockk()
        vehicleController = VehicleController(vehicleService)
        userDetails = mockk()

        every { userDetails.getUserId() } returns 1L
    }

    @Test
    fun `should create vehicle successfully`() {
        val request = CreateVehicleRequest(
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        val response = VehicleResponse(
            id = 1L,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { vehicleService.createVehicle(request, 1L) } returns response

        val result = vehicleController.createVehicle(request, userDetails)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(response, result.body)

        verify { vehicleService.createVehicle(request, 1L) }
        verify { userDetails.getUserId() }
    }

    @Test
    fun `should get all vehicles`() {
        val vehicles = listOf(
            VehicleResponse(
                id = 1L,
                make = "Toyota",
                model = "Camry",
                year = 2020,
                licensePlate = "ABC123",
                fuelType = "GASOLINE",
                tankCapacity = 60.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            VehicleResponse(
                id = 2L,
                make = "Honda",
                model = "Civic",
                year = 2021,
                licensePlate = "XYZ789",
                fuelType = "GASOLINE",
                tankCapacity = 50.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        every { vehicleService.getAllVehicles(1L) } returns vehicles

        val result = vehicleController.getAllVehicles(userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(2, result.body?.size)
        assertEquals(vehicles, result.body)

        verify { vehicleService.getAllVehicles(1L) }
    }

    @Test
    fun `should get vehicle by id`() {
        val vehicle = VehicleResponse(
            id = 1L,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { vehicleService.getVehicleById(1L, 1L) } returns vehicle

        val result = vehicleController.getVehicleById(1L, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(vehicle, result.body)

        verify { vehicleService.getVehicleById(1L, 1L) }
    }

    @Test
    fun `should update vehicle`() {
        val request = UpdateVehicleRequest(
            year = 2021,
            tankCapacity = 65.0
        )

        val response = VehicleResponse(
            id = 1L,
            make = "Toyota",
            model = "Camry",
            year = 2021,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 65.0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { vehicleService.updateVehicle(1L, request, 1L) } returns response

        val result = vehicleController.updateVehicle(1L, request, userDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response, result.body)

        verify { vehicleService.updateVehicle(1L, request, 1L) }
    }

    @Test
    fun `should delete vehicle`() {
        every { vehicleService.deleteVehicle(1L, 1L) } just Runs

        val result = vehicleController.deleteVehicle(1L, userDetails)

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
        assertNull(result.body)

        verify { vehicleService.deleteVehicle(1L, 1L) }
    }

    @Test
    fun `should throw exception when user id is null`() {
        every { userDetails.getUserId() } returns null

        val request = CreateVehicleRequest(
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        assertThrows(IllegalStateException::class.java) {
            vehicleController.createVehicle(request, userDetails)
        }

        verify(exactly = 0) { vehicleService.createVehicle(any(), any()) }
    }

    @Test
    fun `should handle service exception when creating vehicle`() {
        val request = CreateVehicleRequest(
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        every { vehicleService.createVehicle(request, 1L) } throws IllegalArgumentException("Vehicle with this license plate already exists")

        assertThrows(IllegalArgumentException::class.java) {
            vehicleController.createVehicle(request, userDetails)
        }

        verify { vehicleService.createVehicle(request, 1L) }
    }

    @Test
    fun `should handle service exception when getting vehicle`() {
        every { vehicleService.getVehicleById(1L, 1L) } throws IllegalArgumentException("Vehicle not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            vehicleController.getVehicleById(1L, userDetails)
        }

        verify { vehicleService.getVehicleById(1L, 1L) }
    }

    @Test
    fun `should handle service exception when updating vehicle`() {
        val request = UpdateVehicleRequest(year = 2021)

        every { vehicleService.updateVehicle(1L, request, 1L) } throws IllegalArgumentException("Vehicle not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            vehicleController.updateVehicle(1L, request, userDetails)
        }

        verify { vehicleService.updateVehicle(1L, request, 1L) }
    }

    @Test
    fun `should handle service exception when deleting vehicle`() {
        every { vehicleService.deleteVehicle(1L, 1L) } throws IllegalArgumentException("Vehicle not found or access denied")

        assertThrows(IllegalArgumentException::class.java) {
            vehicleController.deleteVehicle(1L, userDetails)
        }

        verify { vehicleService.deleteVehicle(1L, 1L) }
    }
}
