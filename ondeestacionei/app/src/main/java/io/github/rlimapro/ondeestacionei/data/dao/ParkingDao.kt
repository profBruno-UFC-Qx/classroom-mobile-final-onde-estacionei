package io.github.rlimapro.ondeestacionei.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingDao {
    @Query("SELECT * FROM parking_locations ORDER BY timestamp DESC")
    fun getAllLocations(): Flow<List<ParkingLocation>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertLocation(location: ParkingLocation)

    @Delete
    suspend fun deleteLocation(location: ParkingLocation)

    @Update
    suspend fun updateLocation(location: ParkingLocation)
}