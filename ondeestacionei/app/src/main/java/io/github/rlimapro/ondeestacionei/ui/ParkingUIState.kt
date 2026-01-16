package io.github.rlimapro.ondeestacionei.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.android.gms.maps.model.LatLng
import io.github.rlimapro.ondeestacionei.model.ParkingLocation

enum class TransportMode(
    val profile: String,
    val label: String,
    val icon: ImageVector
) {
    WALKING("foot-walking", "A pé", Icons.Filled.DirectionsWalk),
    DRIVING("driving-car", "Carro/Moto", Icons.Filled.DirectionsCar)
}
data class ParkingUiState(
    val routePoints: List<LatLng> = emptyList(),
    val locations: List<ParkingLocation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastLocation: ParkingLocation? = null,
    val currentMode: TransportMode = TransportMode.WALKING,
    val isOnline: Boolean = true,
    val isGpsEnabled: Boolean = true
)