package io.github.rlimapro.ondeestacionei.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import io.github.rlimapro.ondeestacionei.ui.ParkingUiState
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel

@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    carLocation: ParkingLocation,
    state: ParkingUiState,
    viewModel: ParkingViewModel
) {
    val carLatLng = LatLng(carLocation.latitude, carLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(carLatLng, 16f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                compassEnabled = true
            )
        ) {
            // Marcador do carro
            Marker(
                state = MarkerState(position = carLatLng),
                title = "Meu Carro",
                snippet = carLocation.note ?: "Estacionado aqui",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )

            // Rota
            if (state.routePoints.isNotEmpty()) {
                Polyline(
                    points = state.routePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 14f,
                    geodesic = true
                )
            }
        }

        // Informações da localização
        CarInfoCard(
            location = carLocation,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        // Seletor de modo de transporte
        TransportModeSelector(
            currentMode = state.currentMode,
            onModeSelected = { viewModel.updateTransportMode(it) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
    }
}