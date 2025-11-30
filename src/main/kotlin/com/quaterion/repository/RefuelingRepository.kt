package com.quaterion.repository

import com.quaterion.entity.Refueling
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Repository for [Refueling] entity data access.
 *
 * Provides methods for refueling lookup with user ownership verification
 * and date range filtering for analytics.
 */
@Repository
interface RefuelingRepository : JpaRepository<Refueling, Long> {
    /**
     * Finds all refuelings for a vehicle ordered by date descending.
     *
     * @param vehicleId ID of the vehicle
     * @return list of refuelings ordered by date descending
     */
    fun findByVehicleIdOrderByDateDesc(vehicleId: Long): List<Refueling>

    /**
     * Finds refuelings for a vehicle within a date range ordered by date ascending.
     *
     * @param vehicleId ID of the vehicle
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return list of refuelings within the date range
     */
    fun findByVehicleIdAndDateBetweenOrderByDateAsc(
        vehicleId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Refueling>

    /**
     * Finds all refuelings for a vehicle with user ownership verification.
     *
     * @param vehicleId ID of the vehicle
     * @param userId ID of the user for ownership check
     * @return list of refuelings ordered by date descending
     */
    @Query("SELECT r FROM Refueling r WHERE r.vehicle.id = :vehicleId AND r.vehicle.user.id = :userId ORDER BY r.date DESC")
    fun findByVehicleIdAndUserId(@Param("vehicleId") vehicleId: Long, @Param("userId") userId: Long): List<Refueling>

    /**
     * Finds a refueling by ID with user ownership verification.
     *
     * @param id refueling ID
     * @param userId user ID for ownership check
     * @return the [Refueling] if found and owned by the user, null otherwise
     */
    @Query("SELECT r FROM Refueling r WHERE r.id = :id AND r.vehicle.user.id = :userId")
    fun findByIdAndUserId(@Param("id") id: Long, @Param("userId") userId: Long): Refueling?

    /**
     * Finds the refueling with the highest odometer reading for a vehicle.
     *
     * @param vehicleId ID of the vehicle
     * @return the refueling with highest odometer, null if no refuelings exist
     */
    fun findFirstByVehicleIdOrderByOdometerDesc(vehicleId: Long): Refueling?
}
