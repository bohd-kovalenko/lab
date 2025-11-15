package com.quaterion.repository

import com.quaterion.entity.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepository : JpaRepository<Vehicle, Long> {
    fun findByUserId(userId: Long): List<Vehicle>
    fun findByIdAndUserId(id: Long, userId: Long): Vehicle?
    fun existsByLicensePlate(licensePlate: String): Boolean
}
