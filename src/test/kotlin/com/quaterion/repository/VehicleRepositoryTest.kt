package com.quaterion.repository

import com.quaterion.entity.User
import com.quaterion.entity.Vehicle
import com.quaterion.entity.Refueling
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.ActiveProfiles
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class VehicleRepositoryTest {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var vehicleRepository: VehicleRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User
    private lateinit var anotherUser: User

    @BeforeEach
    fun setup() {
        testUser = User(username = "testuser", password = "password", role = "USER")
        entityManager.persist(testUser)

        anotherUser = User(username = "anotheruser", password = "password", role = "USER")
        entityManager.persist(anotherUser)

        entityManager.flush()
    }

    @Test
    fun `should find vehicles by user id`() {
        val vehicle1 = vehicleRepository.save(
            Vehicle(
                user = testUser,
                make = "Toyota",
                model = "Camry",
                year = 2020,
                fuelType = "GASOLINE",
                tankCapacity = 60.0
            )
        )

        val vehicle2 = vehicleRepository.save(
            Vehicle(
                user = testUser,
                make = "Honda",
                model = "Civic",
                year = 2021,
                fuelType = "GASOLINE",
                tankCapacity = 50.0
            )
        )

        val vehicle3 = vehicleRepository.save(
            Vehicle(
                user = anotherUser,
                make = "Ford",
                model = "F150",
                year = 2022,
                fuelType = "DIESEL",
                tankCapacity = 80.0
            )
        )

        val userVehicles = vehicleRepository.findByUserId(testUser.id!!)

        assertEquals(2, userVehicles.size)
        assertTrue(userVehicles.any { it.id == vehicle1.id })
        assertTrue(userVehicles.any { it.id == vehicle2.id })
        assertFalse(userVehicles.any { it.id == vehicle3.id })
    }

    @Test
    fun `should find vehicle by id and user id`() {
        val vehicle = vehicleRepository.save(
            Vehicle(
                user = testUser,
                make = "Toyota",
                model = "Camry",
                year = 2020,
                fuelType = "GASOLINE",
                tankCapacity = 60.0
            )
        )

        val found = vehicleRepository.findByIdAndUserId(vehicle.id!!, testUser.id!!)

        assertNotNull(found)
        assertEquals(vehicle.id, found?.id)
        assertEquals("Toyota", found?.make)
    }

    @Test
    fun `should return null when finding vehicle by id and wrong user id`() {
        val vehicle = vehicleRepository.save(
            Vehicle(
                user = testUser,
                make = "Toyota",
                model = "Camry",
                year = 2020,
                fuelType = "GASOLINE",
                tankCapacity = 60.0
            )
        )

        val found = vehicleRepository.findByIdAndUserId(vehicle.id!!, anotherUser.id!!)

        assertNull(found)
    }

    @Test
    fun `should check if license plate exists`() {
        vehicleRepository.save(
            Vehicle(
                user = testUser,
                make = "Toyota",
                model = "Camry",
                year = 2020,
                licensePlate = "ABC123",
                fuelType = "GASOLINE",
                tankCapacity = 60.0
            )
        )

        assertTrue(vehicleRepository.existsByLicensePlate("ABC123"))
        assertFalse(vehicleRepository.existsByLicensePlate("XYZ789"))
    }

    @Test
    fun `should return empty list for user with no vehicles`() {
        val vehicles = vehicleRepository.findByUserId(testUser.id!!)

        assertTrue(vehicles.isEmpty())
    }

    @Test
    fun `should handle null license plate`() {
        val vehicle = vehicleRepository.save(
            Vehicle(
                user = testUser,
                make = "Toyota",
                model = "Camry",
                year = 2020,
                licensePlate = null,
                fuelType = "GASOLINE",
                tankCapacity = 60.0
            )
        )

        assertNotNull(vehicle.id)
        assertNull(vehicle.licensePlate)
    }
}
