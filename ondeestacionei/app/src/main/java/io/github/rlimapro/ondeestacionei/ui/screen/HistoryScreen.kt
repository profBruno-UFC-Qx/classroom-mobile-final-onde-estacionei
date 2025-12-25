package io.github.rlimapro.ondeestacionei.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import io.github.rlimapro.ondeestacionei.ui.ParkingViewModel
import io.github.rlimapro.ondeestacionei.ui.components.ParkingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: ParkingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var locationToEdit by remember { mutableStateOf<ParkingLocation?>(null) }
    var noteText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Histórico de Estacionamento")
            })
        }
    ) { padding ->
        if (state.locations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Nenhum local salvo ainda.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
                items(state.locations) { location ->
                    ParkingItem(
                        location = location,
                        onDelete = { viewModel.deleteLocation(location) },
                        onEdit = {
                            locationToEdit = location
                            noteText = location.note ?: ""
                        }
                    )
                }
            }

            if (locationToEdit != null) {
                AlertDialog(
                    onDismissRequest = { locationToEdit = null },
                    title = { Text("Editar Nota") },
                    text = {
                        TextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text("Ex: Vaga G42, Andar 3") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            locationToEdit?.let {
                                viewModel.updateLocation(it.copy(note = noteText))
                            }
                            locationToEdit = null
                        }) { Text("Salvar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { locationToEdit = null }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}