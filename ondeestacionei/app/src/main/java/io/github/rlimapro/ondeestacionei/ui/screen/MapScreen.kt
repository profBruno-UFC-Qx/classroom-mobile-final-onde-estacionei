package io.github.rlimapro.ondeestacionei.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: ParkingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val carLocation = state.lastLocation

    if (carLocation == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhuma localização de carro salva.")
        }
    } else {
        val carLatLng = LatLng(carLocation.latitude, carLocation.longitude)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(carLatLng, 17f)
        }

        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text("Rota até o Carro")
                })
            }
        ) { padding ->
            GoogleMap(
                modifier = Modifier.fillMaxSize().padding(padding),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true)
            ) {
                Marker(
                    state = MarkerState(position = carLatLng),
                    title = "Meu Carro",
                    snippet = carLocation.note ?: "Estacionado aqui"
                )
            }
        }
    }
}