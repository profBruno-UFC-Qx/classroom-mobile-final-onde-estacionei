package io.github.rlimapro.ondeestacionei.ui.screen

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import io.github.rlimapro.ondeestacionei.BuildConfig
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel
import io.github.rlimapro.ondeestacionei.ui.TransportMode

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: ParkingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val carLocation = state.lastLocation
    val context = LocalContext.current

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val orsKey = BuildConfig.ORS_API_KEY

    DisposableEffect(carLocation, state.currentMode) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateDistanceMeters(10f)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val userLoc = result.lastLocation ?: return
                val carLoc = carLocation ?: return

                viewModel.fetchRoute(
                    start = LatLng(userLoc.latitude, userLoc.longitude),
                    end = LatLng(carLoc.latitude, carLoc.longitude),
                    apiKey = orsKey
                )
            }
        }

        if (carLocation != null) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rota até o Carro") })
        }
    ) { padding ->
        if (carLocation == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma localização de carro salva.")
            }
        } else {
            val carLatLng = LatLng(carLocation.latitude, carLocation.longitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(carLatLng, 17f)
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = true)
                ) {
                    Marker(
                        state = MarkerState(position = carLatLng),
                        title = "Meu Carro",
                        snippet = carLocation.note ?: "Estacionado aqui"
                    )

                    if (state.routePoints.isNotEmpty()) {
                        Polyline(
                            points = state.routePoints,
                            color = MaterialTheme.colorScheme.primary,
                            width = 12f,
                            geodesic = true
                        )
                    }
                }

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    TransportMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = state.currentMode == mode,
                            onClick = { viewModel.updateTransportMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = TransportMode.entries.size
                            ),
                            label = { Text(mode.label) }
                        )
                    }
                }
            }
        }
    }
}