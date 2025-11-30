package com.quaterion.repository

import com.quaterion.entity.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for [Vehicle] entity data access.
 *
 * Provides methods for vehicle lookup with user ownership verification.
 */
@Repository
interface VehicleRepository : JpaRepository<Vehicle, Long> {
    /**
     * Finds all vehicles belonging to a user.
     *
     * @param userId ID of the user
     * @return list of vehicles owned by the user
     */
    fun findByUserId(userId: Long): List<Vehicle>

    /**
     * Finds a vehicle by ID with user ownership verification.
     *
     * @param id vehicle ID
     * @param userId user ID for ownership check
     * @return the [Vehicle] if found and owned by the user, null otherwise
     */
    fun findByIdAndUserId(id: Long, userId: Long): Vehicle?

    /**
     * Checks if a vehicle with the given license plate exists.
     *
     * @param licensePlate the license plate to check
     * @return true if a vehicle with the license plate exists, false otherwise
     */
    fun existsByLicensePlate(licensePlate: String): Boolean
}
