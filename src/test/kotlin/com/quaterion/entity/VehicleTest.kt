package com.quaterion.entity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class VehicleTest {

    @Test
    fun `should create vehicle with all required fields`() {
        val user = User(id = 1L, username = "testuser", password = "password", role = "USER")
        val now = LocalDateTime.now()

        val vehicle = Vehicle(
            id = 1L,
            user = user,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            licensePlate = "ABC123",
            fuelType = "GASOLINE",
            tankCapacity = 60.0,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(1L, vehicle.id)
        assertEquals(user, vehicle.user)
        assertEquals("Toyota", vehicle.make)
        assertEquals("Camry", vehicle.model)
        assertEquals(2020, vehicle.year)
        assertEquals("ABC123", vehicle.licensePlate)
        assertEquals("GASOLINE", vehicle.fuelType)
        assertEquals(60.0, vehicle.tankCapacity)
        assertEquals(now, vehicle.createdAt)
        assertEquals(now, vehicle.updatedAt)
    }

    @Test
    fun `should create vehicle without license plate`() {
        val user = User(id = 1L, username = "testuser", password = "password", role = "USER")

        val vehicle = Vehicle(
            user = user,
            make = "Honda",
            model = "Civic",
            year = 2021,
            licensePlate = null,
            fuelType = "GASOLINE",
            tankCapacity = 50.0
        )

        assertNull(vehicle.licensePlate)
        assertNotNull(vehicle.createdAt)
        assertNotNull(vehicle.updatedAt)
    }

    @Test
    fun `should support different fuel types`() {
        val user = User(id = 1L, username = "testuser", password = "password", role = "USER")

        val dieselVehicle = Vehicle(
            user = user,
            make = "Ford",
            model = "F150",
            year = 2022,
            fuelType = "DIESEL",
            tankCapacity = 80.0
        )

        val electricVehicle = Vehicle(
            user = user,
            make = "Tesla",
            model = "Model 3",
            year = 2023,
            fuelType = "ELECTRIC",
            tankCapacity = 75.0
        )

        assertEquals("DIESEL", dieselVehicle.fuelType)
        assertEquals("ELECTRIC", electricVehicle.fuelType)
    }

    @Test
    fun `should copy vehicle with updated fields`() {
        val user = User(id = 1L, username = "testuser", password = "password", role = "USER")
        val original = Vehicle(
            id = 1L,
            user = user,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )

        val updated = original.copy(
            year = 2021,
            licensePlate = "XYZ789"
        )

        assertEquals(2021, updated.year)
        assertEquals("XYZ789", updated.licensePlate)
        assertEquals(original.make, updated.make)
        assertEquals(original.model, updated.model)
    }
}
