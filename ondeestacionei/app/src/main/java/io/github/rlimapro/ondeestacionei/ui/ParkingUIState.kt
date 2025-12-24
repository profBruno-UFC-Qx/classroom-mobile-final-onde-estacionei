package io.github.rlimapro.ondeestacionei.ui

import com.google.android.gms.maps.model.LatLng
import io.github.rlimapro.ondeestacionei.model.ParkingLocation

data class ParkingUiState(
    val routePoints: List<LatLng> = emptyList(),
    val locations: List<ParkingLocation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastLocation: ParkingLocation? = null
)