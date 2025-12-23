package io.github.rlimapro.ondeestacionei.data.repository

import io.github.rlimapro.ondeestacionei.data.dao.ParkingDao
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import kotlinx.coroutines.flow.Flow

class ParkingRepository(private val parkingDao: ParkingDao) {

    val allLocations: Flow<List<ParkingLocation>> = parkingDao.getAllLocations()

    suspend fun insert(location: ParkingLocation) = parkingDao.insertLocation(location)

    suspend fun update(location: ParkingLocation) = parkingDao.updateLocation(location)

    suspend fun delete(location: ParkingLocation) = parkingDao.deleteLocation(location)
}