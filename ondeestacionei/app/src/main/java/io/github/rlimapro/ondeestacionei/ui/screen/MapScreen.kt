package io.github.rlimapro.ondeestacionei.ui.screen

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel
import io.github.rlimapro.ondeestacionei.ui.components.EmptyMapState
import io.github.rlimapro.ondeestacionei.ui.components.MapContent
import io.github.rlimapro.ondeestacionei.ui.components.MapTopBar

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: ParkingViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val carLocation = state.lastLocation
    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

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
                    end = LatLng(carLoc.latitude, carLoc.longitude)
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
            MapTopBar(
                onNavigateBack = onNavigateBack,
                carLocation = carLocation
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (carLocation == null) {
            EmptyMapState(modifier = Modifier.padding(padding))
        } else {
            MapContent(
                modifier = Modifier.padding(padding),
                carLocation = carLocation,
                state = state,
                viewModel = viewModel
            )
        }
    }
}