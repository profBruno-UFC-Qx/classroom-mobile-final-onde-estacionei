package io.github.rlimapro.ondeestacionei.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel
import io.github.rlimapro.ondeestacionei.ui.components.LastParkingCard
import io.github.rlimapro.ondeestacionei.ui.components.MainTopBar
import io.github.rlimapro.ondeestacionei.ui.components.ParkingButton
import io.github.rlimapro.ondeestacionei.ui.components.dialog.ParkingNoteDialog

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ParkingViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val showNotePref by viewModel.showNotePreference.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var tempNote by remember { mutableStateOf("") }
    var lastKnownLocation by remember { mutableStateOf<android.location.Location?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun checkLocationSettings() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsRequest)
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(context as android.app.Activity, 1001)
                    } catch (sendEx: Exception) {
                        android.util.Log.e("MainScreen", "Erro ao abrir diálogo de GPS", sendEx)
                    }
                }
            }
    }

    if (showDialog) {
        ParkingNoteDialog(
            note = tempNote,
            onNoteChange = { tempNote = it },
            showNotePref = showNotePref,
            onTogglePreference = { viewModel.toggleNotePreference(!it) },
            onConfirm = {
                lastKnownLocation?.let { loc ->
                    viewModel.addLocation(loc.latitude, loc.longitude, tempNote)
                }
                showDialog = false
                tempNote = ""
            },
            onSkip = {
                lastKnownLocation?.let { loc ->
                    viewModel.addLocation(loc.latitude, loc.longitude, null)
                }
                showDialog = false
                tempNote = ""
            },
            onDismiss = {
                showDialog = false
                tempNote = ""
            }
        )
    }

    Scaffold(
        topBar = {
            MainTopBar(onNavigateToHistory = onNavigateToHistory)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF6650a4).copy(alpha = 0.05f),
                    radius = size.width * 0.6f,
                    center = Offset(size.width * 0.5f, -size.height * 0.2f)
                )
                drawCircle(
                    color = Color(0xFF6650a4).copy(alpha = 0.03f),
                    radius = size.width * 0.8f,
                    center = Offset(-size.width * 0.3f, size.height * 0.8f)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (!state.isOnline) {
                        StatusBanner(
                            text = "Sem conexão com a internet",
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    if (!state.isGpsEnabled) {
                        StatusBanner(
                            text = "GPS desativado. Toque para ativar.",
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            onClick = { checkLocationSettings() }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedVisibility(
                        visible = state.lastLocation != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        state.lastLocation?.let { lastLoc ->
                            LastParkingCard(
                                location = lastLoc,
                                onNavigateToMap = onNavigateToMap
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    ParkingButton(
                        onClick = {
                            handleParkingButtonClick(
                                locationPermissionState = locationPermissionState,
                                fusedLocationClient = fusedLocationClient,
                                onLocationFetched = { location ->
                                    lastKnownLocation = location

                                    if (showNotePref) {
                                        showDialog = true
                                    } else {
                                        viewModel.addLocation(location.latitude, location.longitude, null)
                                    }
                                }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun StatusBanner(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = containerColor,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun handleParkingButtonClick(
    locationPermissionState: PermissionState,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFetched: (android.location.Location) -> Unit
) {
    if (locationPermissionState.status.isGranted) {
        getCurrentLocation(fusedLocationClient) { location ->
            onLocationFetched(location)
        }
    } else {
        locationPermissionState.launchPermissionRequest()
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (android.location.Location) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReceived(location)
        } else {
            val priority = Priority.PRIORITY_HIGH_ACCURACY
            fusedLocationClient.getCurrentLocation(priority, null)
                .addOnSuccessListener { freshLocation ->
                    freshLocation?.let { onLocationReceived(it) }
                }
        }
    }
}