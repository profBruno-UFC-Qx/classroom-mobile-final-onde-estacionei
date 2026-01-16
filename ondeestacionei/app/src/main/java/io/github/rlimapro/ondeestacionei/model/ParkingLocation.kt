package io.github.rlimapro.ondeestacionei.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_locations")
data class ParkingLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val address: String?,
    val note: String?
)