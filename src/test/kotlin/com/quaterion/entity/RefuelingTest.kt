package com.quaterion.entity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class RefuelingTest {

    private fun createTestVehicle(): Vehicle {
        val user = User(id = 1L, username = "testuser", password = "password", role = "USER")
        return Vehicle(
            id = 1L,
            user = user,
            make = "Toyota",
            model = "Camry",
            year = 2020,
            fuelType = "GASOLINE",
            tankCapacity = 60.0
        )
    }

    @Test
    fun `should create refueling with all required fields`() {
        val vehicle = createTestVehicle()
        val now = LocalDateTime.now()

        val refueling = Refueling(
            id = 1L,
            vehicle = vehicle,
            date = now,
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            totalCost = 68.25,
            notes = "Test refueling",
            fullTank = true,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(1L, refueling.id)
        assertEquals(vehicle, refueling.vehicle)
        assertEquals(now, refueling.date)
        assertEquals(10000.0, refueling.odometer)
        assertEquals(45.5, refueling.fuelAmount)
        assertEquals(1.5, refueling.pricePerLiter)
        assertEquals(68.25, refueling.totalCost)
        assertEquals("Test refueling", refueling.notes)
        assertTrue(refueling.fullTank)
    }

    @Test
    fun `should create refueling without notes`() {
        val vehicle = createTestVehicle()
        val now = LocalDateTime.now()

        val refueling = Refueling(
            vehicle = vehicle,
            date = now,
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            totalCost = 68.25,
            fullTank = false
        )

        assertNull(refueling.notes)
        assertFalse(refueling.fullTank)
    }

    @Test
    fun `should calculate total cost correctly`() {
        val vehicle = createTestVehicle()
        val fuelAmount = 50.0
        val pricePerLiter = 1.75
        val expectedTotal = 87.5

        val refueling = Refueling(
            vehicle = vehicle,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = fuelAmount,
            pricePerLiter = pricePerLiter,
            totalCost = fuelAmount * pricePerLiter,
            fullTank = true
        )

        assertEquals(expectedTotal, refueling.totalCost)
    }

    @Test
    fun `should copy refueling with updated fields`() {
        val vehicle = createTestVehicle()
        val original = Refueling(
            id = 1L,
            vehicle = vehicle,
            date = LocalDateTime.now(),
            odometer = 10000.0,
            fuelAmount = 45.5,
            pricePerLiter = 1.5,
            totalCost = 68.25,
            fullTank = false
        )

        val updated = original.copy(
            odometer = 10500.0,
            fuelAmount = 50.0,
            totalCost = 75.0,
            fullTank = true
        )

        assertEquals(10500.0, updated.odometer)
        assertEquals(50.0, updated.fuelAmount)
        assertEquals(75.0, updated.totalCost)
        assertTrue(updated.fullTank)
        assertEquals(original.vehicle, updated.vehicle)
        assertEquals(original.pricePerLiter, updated.pricePerLiter)
    }

    @Test
    fun `should handle different odometer readings`() {
        val vehicle = createTestVehicle()

        val refueling1 = Refueling(
            vehicle = vehicle,
            date = LocalDateTime.now(),
            odometer = 5000.0,
            fuelAmount = 40.0,
            pricePerLiter = 1.5,
            totalCost = 60.0,
            fullTank = true
        )

        val refueling2 = Refueling(
            vehicle = vehicle,
            date = LocalDateTime.now(),
            odometer = 5500.5,
            fuelAmount = 45.0,
            pricePerLiter = 1.5,
            totalCost = 67.5,
            fullTank = true
        )

        assertTrue(refueling2.odometer > refueling1.odometer)
        assertEquals(500.5, refueling2.odometer - refueling1.odometer, 0.01)
    }
}
