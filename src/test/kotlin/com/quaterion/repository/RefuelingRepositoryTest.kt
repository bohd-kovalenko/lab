package com.quaterion.repository

import com.quaterion.entity.Refueling
import com.quaterion.entity.User
import com.quaterion.entity.Vehicle
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.ActiveProfiles
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.time.LocalDateTime

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RefuelingRepositoryTest {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var refuelingRepository: RefuelingRepository

    @Autowired
    private lateinit var vehicleRepository: VehicleRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User
    private lateinit var anotherUser: User
    private lateinit var testVehicle: Vehicle
    private lateinit var anotherVehicle: Vehicle

    @BeforeEach
    fun setup() {
        testUser = User(username = "testuser", password = "password", role = "USER")
        entityManager.persist(testUser)

        anotherUser = User(username = "anotheruser", password = "password", role = "USER")
        entityManager.persist(anotherUser)

        entityManager.flush()

        testVehicle = Vehicle(
            user = testUser,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )
        entityManager.persist(testVehicle)

        anotherVehicle = Vehicle(
            user = anotherUser,
            make = "Honda",
            model = "Civic",
            year = 2021,
            fuelType = "GASOLINE",
            tankCapacity = 50.0
        )
        entityManager.persist(anotherVehicle)

        entityManager.flush()
    }

    @Test
    fun `should find refuelings by vehicle id ordered by date desc`() {
        val now = LocalDateTime.now()
        val refueling1 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = now.minusDays(2),
                odometer = 10000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                fullTank = true
            )
        )

        val refueling2 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = now.minusDays(1),
                odometer = 10500.0,
                fuelAmount = 45.0,
                pricePerLiter = 1.6,
                totalCost = 72.0,
                fullTank = true
            )
        )

        val refueling3 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = now,
                odometer = 11000.0,
                fuelAmount = 42.0,
                pricePerLiter = 1.55,
                totalCost = 65.1,
                fullTank = true
            )
        )

        val refuelings = refuelingRepository.findByVehicleIdOrderByDateDesc(testVehicle.id!!)

        assertEquals(3, refuelings.size)
        assertEquals(refueling3.id, refuelings[0].id)
        assertEquals(refueling2.id, refuelings[1].id)
        assertEquals(refueling1.id, refuelings[2].id)
    }

    @Test
    fun `should find refuelings by vehicle id and date range`() {
        val now = LocalDateTime.now()
        val refueling1 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = now.minusDays(10),
                odometer = 9000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                fullTank = true
            )
        )

        val refueling2 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = now.minusDays(5),
                odometer = 9500.0,
                fuelAmount = 45.0,
                pricePerLiter = 1.6,
                totalCost = 72.0,
                fullTank = true
            )
        )

        val refueling3 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = now.minusDays(1),
                odometer = 10000.0,
                fuelAmount = 42.0,
                pricePerLiter = 1.55,
                totalCost = 65.1,
                fullTank = true
            )
        )

        val refuelings = refuelingRepository.findByVehicleIdAndDateBetweenOrderByDateAsc(
            testVehicle.id!!,
            now.minusDays(7),
            now
        )

        assertEquals(2, refuelings.size)
        assertTrue(refuelings.any { it.id == refueling2.id })
        assertTrue(refuelings.any { it.id == refueling3.id })
        assertFalse(refuelings.any { it.id == refueling1.id })
    }

    @Test
    fun `should find refuelings by vehicle id and user id`() {
        val refueling1 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = LocalDateTime.now(),
                odometer = 10000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                fullTank = true
            )
        )

        val refueling2 = refuelingRepository.save(
            Refueling(
                vehicle = anotherVehicle,
                date = LocalDateTime.now(),
                odometer = 5000.0,
                fuelAmount = 35.0,
                pricePerLiter = 1.5,
                totalCost = 52.5,
                fullTank = true
            )
        )

        val refuelings = refuelingRepository.findByVehicleIdAndUserId(testVehicle.id!!, testUser.id!!)

        assertEquals(1, refuelings.size)
        assertEquals(refueling1.id, refuelings[0].id)
    }

    @Test
    fun `should return empty list when finding refuelings for wrong user`() {
        refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = LocalDateTime.now(),
                odometer = 10000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                fullTank = true
            )
        )

        val refuelings = refuelingRepository.findByVehicleIdAndUserId(testVehicle.id!!, anotherUser.id!!)

        assertTrue(refuelings.isEmpty())
    }

    @Test
    fun `should find refueling by id and user id`() {
        val refueling = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = LocalDateTime.now(),
                odometer = 10000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                fullTank = true
            )
        )

        val found = refuelingRepository.findByIdAndUserId(refueling.id!!, testUser.id!!)

        assertNotNull(found)
        assertEquals(refueling.id, found?.id)
    }

    @Test
    fun `should return null when finding refueling with wrong user id`() {
        val refueling = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = LocalDateTime.now(),
                odometer = 10000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                fullTank = true
            )
        )

        val found = refuelingRepository.findByIdAndUserId(refueling.id!!, anotherUser.id!!)

        assertNull(found)
    }

    @Test
    fun `should find latest refueling by vehicle id`() {
        val refueling1 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = LocalDateTime.now().minusDays(2),
                odometer = 10000.0,
                fuelAmount = 40.0,
                pricePerLiter = 1.5,
                totalCost = 60.0,
                fullTank = true
            )
        )

        val refueling2 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = LocalDateTime.now().minusDays(1),
                odometer = 10500.0,
                fuelAmount = 45.0,
                pricePerLiter = 1.6,
                totalCost = 72.0,
                fullTank = true
            )
        )

        val refueling3 = refuelingRepository.save(
            Refueling(
                vehicle = testVehicle,
                date = LocalDateTime.now(),
                odometer = 11000.0,
                fuelAmount = 42.0,
                pricePerLiter = 1.55,
                totalCost = 65.1,
                fullTank = true
            )
        )

        val latest = refuelingRepository.findFirstByVehicleIdOrderByOdometerDesc(testVehicle.id!!)

        assertNotNull(latest)
        assertEquals(refueling3.id, latest?.id)
        assertEquals(11000.0, latest?.odometer)
    }

    @Test
    fun `should return null when no refuelings exist for vehicle`() {
        val latest = refuelingRepository.findFirstByVehicleIdOrderByOdometerDesc(testVehicle.id!!)

        assertNull(latest)
    }
}
