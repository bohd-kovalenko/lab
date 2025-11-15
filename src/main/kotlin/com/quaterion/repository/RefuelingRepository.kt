package com.quaterion.repository

import com.quaterion.entity.Refueling
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface RefuelingRepository : JpaRepository<Refueling, Long> {
    fun findByVehicleIdOrderByDateDesc(vehicleId: Long): List<Refueling>

    fun findByVehicleIdAndDateBetweenOrderByDateAsc(
        vehicleId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Refueling>

    @Query("SELECT r FROM Refueling r WHERE r.vehicle.id = :vehicleId AND r.vehicle.user.id = :userId ORDER BY r.date DESC")
    fun findByVehicleIdAndUserId(@Param("vehicleId") vehicleId: Long, @Param("userId") userId: Long): List<Refueling>

    @Query("SELECT r FROM Refueling r WHERE r.id = :id AND r.vehicle.user.id = :userId")
    fun findByIdAndUserId(@Param("id") id: Long, @Param("userId") userId: Long): Refueling?

    fun findFirstByVehicleIdOrderByOdometerDesc(vehicleId: Long): Refueling?
}
