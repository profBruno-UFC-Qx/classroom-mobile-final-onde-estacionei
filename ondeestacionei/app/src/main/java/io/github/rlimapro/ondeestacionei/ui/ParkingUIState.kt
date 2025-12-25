package io.github.rlimapro.ondeestacionei.ui

import com.google.android.gms.maps.model.LatLng
import io.github.rlimapro.ondeestacionei.model.ParkingLocation

enum class TransportMode(val profile: String, val label: String) {
    WALKING("foot-walking", "A pé"),
    DRIVING("driving-car", "Carro/Moto")
}
data class ParkingUiState(
    val routePoints: List<LatLng> = emptyList(),
    val locations: List<ParkingLocation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastLocation: ParkingLocation? = null,
    val currentMode: TransportMode = TransportMode.WALKING
)