package com.quaterion.service

import com.quaterion.dto.CreateVehicleRequest
import com.quaterion.dto.UpdateVehicleRequest
import com.quaterion.entity.User
import com.quaterion.entity.Vehicle
import com.quaterion.repository.UserRepository
import com.quaterion.repository.VehicleRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.*

class VehicleServiceTest {

    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var userRepository: UserRepository
    private lateinit var vehicleService: VehicleService

    private val testUser = User(id = 1L, username = "testuser", password = "password", role = "USER")

    @BeforeEach
    fun setup() {
        vehicleRepository = mockk()
        userRepository = mockk()
        vehicleService = VehicleService(vehicleRepository, userRepository)
    }

    @Test
    fun `should create vehicle successfully`() {
        val request = CreateVehicleRequest(
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "gasoline",
            tankCapacity = 60.0
        )

        every { userRepository.findById(1L) } returns Optional.of(testUser)
        every { vehicleRepository.existsByLicensePlate("ABC123") } returns false
        every { vehicleRepository.save(any<Vehicle>()) } answers {
            firstArg<Vehicle>().copy(id = 1L)
        }

        val response = vehicleService.createVehicle(request, 1L)

        assertEquals(1L, response.id)
        assertEquals("Toyota", response.make)
        assertEquals("Camry", response.model)
        assertEquals(2020, response.year)
        assertEquals("ABC123", response.licensePlate)
        assertEquals("GASOLINE", response.fuelType)
        assertEquals(60.0, response.tankCapacity)

        verify { userRepository.findById(1L) }
        verify { vehicleRepository.existsByLicensePlate("ABC123") }
        verify { vehicleRepository.save(any<Vehicle>()) }
    }

    @Test
    fun `should throw exception when user not found`() {
        val request = CreateVehicleRequest(
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        every { userRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows<IllegalArgumentException> {
            vehicleService.createVehicle(request, 1L)
        }

        assertEquals("User not found", exception.message)
        verify { userRepository.findById(1L) }
        verify(exactly = 0) { vehicleRepository.save(any<Vehicle>()) }
    }

    @Test
    fun `should throw exception when license plate already exists`() {
        val request = CreateVehicleRequest(
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        every { userRepository.findById(1L) } returns Optional.of(testUser)
        every { vehicleRepository.existsByLicensePlate("ABC123") } returns true

        val exception = assertThrows<IllegalArgumentException> {
            vehicleService.createVehicle(request, 1L)
        }

        assertEquals("Vehicle with this license plate already exists", exception.message)
        verify(exactly = 0) { vehicleRepository.save(any<Vehicle>()) }
    }

    @Test
    fun `should get all vehicles for user`() {
        val vehicle1 = Vehicle(
            id = 1L,
            user = testUser,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        val vehicle2 = Vehicle(
            id = 2L,
            user = testUser,
            make = "Honda",
            model = "Civic",
            year = 2021,
            fuelType = "GASOLINE",
            tankCapacity = 50.0
        )

        every { vehicleRepository.findByUserId(1L) } returns listOf(vehicle1, vehicle2)

        val response = vehicleService.getAllVehicles(1L)

        assertEquals(2, response.size)
        assertEquals("Toyota", response[0].make)
        assertEquals("Honda", response[1].make)

        verify { vehicleRepository.findByUserId(1L) }
    }

    @Test
    fun `should get vehicle by id`() {
        val vehicle = Vehicle(
            id = 1L,
            user = testUser,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns vehicle

        val response = vehicleService.getVehicleById(1L, 1L)

        assertEquals(1L, response.id)
        assertEquals("Toyota", response.make)
        assertEquals("Camry", response.model)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
    }

    @Test
    fun `should throw exception when getting vehicle with wrong user`() {
        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            vehicleService.getVehicleById(1L, 1L)
        }

        assertEquals("Vehicle not found or access denied", exception.message)
    }

    @Test
    fun `should update vehicle successfully`() {
        val existingVehicle = Vehicle(
            id = 1L,
            user = testUser,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        val updateRequest = UpdateVehicleRequest(
            year = 2021,
            tankCapacity = 65.0
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns existingVehicle
        every { vehicleRepository.save(any<Vehicle>()) } answers { firstArg() }

        val response = vehicleService.updateVehicle(1L, updateRequest, 1L)

        assertEquals(2021, response.year)
        assertEquals(65.0, response.tankCapacity)
        assertEquals("Toyota", response.make)
        assertEquals("Camry", response.model)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
        verify { vehicleRepository.save(any<Vehicle>()) }
    }

    @Test
    fun `should throw exception when updating vehicle with duplicate license plate`() {
        val existingVehicle = Vehicle(
            id = 1L,
            user = testUser,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        val updateRequest = UpdateVehicleRequest(
            licensePlate = "XYZ789"
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns existingVehicle
        every { vehicleRepository.existsByLicensePlate("XYZ789") } returns true

        val exception = assertThrows<IllegalArgumentException> {
            vehicleService.updateVehicle(1L, updateRequest, 1L)
        }

        assertEquals("Vehicle with this license plate already exists", exception.message)
        verify(exactly = 0) { vehicleRepository.save(any<Vehicle>()) }
    }

    @Test
    fun `should delete vehicle successfully`() {
        val vehicle = Vehicle(
            id = 1L,
            user = testUser,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns vehicle
        every { vehicleRepository.delete(vehicle) } just Runs

        vehicleService.deleteVehicle(1L, 1L)

        verify { vehicleRepository.findByIdAndUserId(1L, 1L) }
        verify { vehicleRepository.delete(vehicle) }
    }

    @Test
    fun `should throw exception when deleting non-existent vehicle`() {
        every { vehicleRepository.findByIdAndUserId(1L, 1L) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            vehicleService.deleteVehicle(1L, 1L)
        }

        assertEquals("Vehicle not found or access denied", exception.message)
        verify(exactly = 0) { vehicleRepository.delete(any()) }
    }

    @Test
    fun `should convert fuel type to uppercase when creating vehicle`() {
        val request = CreateVehicleRequest(
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "diesel",
            tankCapacity = 60.0
        )

        every { userRepository.findById(1L) } returns Optional.of(testUser)
        every { vehicleRepository.existsByLicensePlate(any()) } returns false
        every { vehicleRepository.save(any<Vehicle>()) } answers {
            firstArg<Vehicle>().copy(id = 1L)
        }

        val response = vehicleService.createVehicle(request, 1L)

        assertEquals("DIESEL", response.fuelType)
    }
}
