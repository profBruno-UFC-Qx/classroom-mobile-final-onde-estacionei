package io.github.rlimapro.ondeestacionei.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel

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
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                tempNote = ""
            },
            title = { Text("Adicionar Nota") },
            text = {
                Column {
                    TextField(
                        value = tempNote,
                        onValueChange = { tempNote = it },
                        placeholder = { Text("Ex: Vaga G42, 3º Andar") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = !showNotePref,
                            onCheckedChange = { viewModel.toggleNotePreference(!it) }
                        )
                        Text("Não perguntar novamente", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    lastKnownLocation?.let {
                        viewModel.addLocation(it.latitude, it.longitude, tempNote)
                    }
                    showDialog = false
                    tempNote = ""
                }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    lastKnownLocation?.let {
                        viewModel.addLocation(it.latitude, it.longitude, null)
                    }
                    showDialog = false
                    tempNote = ""
                }) { Text("Pular") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Onde Estacionei?") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "Histórico")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            state.lastLocation?.let { lastLoc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Último Estacionamento:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = lastLoc.address ?: "Localização Guardada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        if (!lastLoc.note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "Nota: ${lastLoc.note}",
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToMap,
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Ver no Mapa / Rota")
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (locationPermissionState.status.isGranted) {
                        getCurrentLocation(fusedLocationClient) { location ->
                            if (showNotePref) {
                                lastKnownLocation = location
                                showDialog = true
                            } else {
                                viewModel.addLocation(location.latitude, location.longitude, null)
                            }
                        }
                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier.size(200.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("ESTACIONEI!")
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationReceived: (android.location.Location) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let { onLocationReceived(it) }
    }
}