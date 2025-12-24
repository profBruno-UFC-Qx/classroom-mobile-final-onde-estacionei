package io.github.rlimapro.ondeestacionei.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.rlimapro.ondeestacionei.data.AppDatabase
import io.github.rlimapro.ondeestacionei.data.repository.ParkingRepository
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ParkingRepository

    private val _uiState = MutableStateFlow(ParkingUiState())

    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).parkingDao()
        repository = ParkingRepository(dao)

        observeLocations()
    }

    private fun observeLocations() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.allLocations.collect { list ->
                _uiState.update { currentState ->
                    currentState.copy(
                        locations = list,
                        isLoading = false,
                        lastLocation = list.firstOrNull()
                    )
                }
            }
        }
    }

    fun addLocation(lat: Double, lng: Double, note: String? = null) {
        viewModelScope.launch {
            try {
                val newLocation = ParkingLocation(
                    latitude = lat,
                    longitude = lng,
                    timestamp = System.currentTimeMillis(),
                    address = "Processando endereço...",
                    note = note
                )
                repository.insert(newLocation)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao salvar: ${e.message}") }
            }
        }
    }

    fun deleteLocation(location: ParkingLocation) {
        viewModelScope.launch {
            repository.delete(location)
        }
    }

    fun updateLocation(location: ParkingLocation) {
        viewModelScope.launch {
            repository.update(location)
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}