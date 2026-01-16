package io.github.rlimapro.ondeestacionei.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import io.github.rlimapro.ondeestacionei.ui.components.EmptyHistoryState
import io.github.rlimapro.ondeestacionei.ui.components.HistoryTopBar
import io.github.rlimapro.ondeestacionei.ui.components.ParkingHistoryItem
import io.github.rlimapro.ondeestacionei.ui.components.dialog.DeleteConfirmationDialog
import io.github.rlimapro.ondeestacionei.ui.components.dialog.EditNoteDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ParkingViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val parkingHistory = state.locations

    var locationToEdit by remember { mutableStateOf<ParkingLocation?>(null) }
    var noteText by remember { mutableStateOf("") }
    var locationToDelete by remember { mutableStateOf<ParkingLocation?>(null) }

    // Dialog de edição de nota
    if (locationToEdit != null) {
        EditNoteDialog(
            note = noteText,
            onNoteChange = { noteText = it },
            onConfirm = {
                locationToEdit?.let {
                    viewModel.updateLocation(it.copy(note = noteText))
                }
                locationToEdit = null
                noteText = ""
            },
            onDismiss = {
                locationToEdit = null
                noteText = ""
            }
        )
    }

    // Dialog de confirmação de exclusão
    if (locationToDelete != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                locationToDelete?.let { viewModel.deleteLocation(it) }
                locationToDelete = null
            },
            onDismiss = {
                locationToDelete = null
            }
        )
    }

    Scaffold(
        topBar = {
            HistoryTopBar(
                onNavigateBack = onNavigateBack,
                itemCount = parkingHistory.size
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (parkingHistory.isEmpty()) {
                EmptyHistoryState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = parkingHistory,
                        key = { it.id }
                    ) { location ->
                        ParkingHistoryItem(
                            location = location,
                            onEdit = {
                                locationToEdit = location
                                noteText = location.note ?: ""
                            },
                            onDelete = {
                                locationToDelete = location
                            }
                        )
                    }
                }
            }
        }
    }
}