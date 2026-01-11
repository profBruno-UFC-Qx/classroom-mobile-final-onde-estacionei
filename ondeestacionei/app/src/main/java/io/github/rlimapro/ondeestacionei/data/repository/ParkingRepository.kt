package io.github.rlimapro.ondeestacionei.data.repository

import com.google.android.gms.maps.model.LatLng
import io.github.rlimapro.ondeestacionei.data.dao.ParkingDao
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import io.github.rlimapro.ondeestacionei.network.datasource.OrsApiService
import kotlinx.coroutines.flow.Flow

class ParkingRepository(
    private val parkingDao: ParkingDao,
    private val orsApi: OrsApiService
) {

    val allLocations: Flow<List<ParkingLocation>> = parkingDao.getAllLocations()
    suspend fun insert(location: ParkingLocation) = parkingDao.insertLocation(location)
    suspend fun update(location: ParkingLocation) = parkingDao.updateLocation(location)
    suspend fun delete(location: ParkingLocation) = parkingDao.deleteLocation(location)

    suspend fun getRoutePoints(start: LatLng, end: LatLng, mode: String): List<LatLng> {
        val response = orsApi.getRoute(
            profile = mode,
            start = "${start.longitude},${start.latitude}",
            end = "${end.longitude},${end.latitude}"
        )
        return response.features.firstOrNull()?.geometry?.coordinates?.map {
            LatLng(it[1], it[0])
        } ?: emptyList()
    }
}