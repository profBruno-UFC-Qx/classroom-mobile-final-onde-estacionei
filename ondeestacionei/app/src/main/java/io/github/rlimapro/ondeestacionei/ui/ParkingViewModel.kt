package io.github.rlimapro.ondeestacionei.ui

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import io.github.rlimapro.ondeestacionei.data.AppDatabase
import io.github.rlimapro.ondeestacionei.data.SettingsManager
import io.github.rlimapro.ondeestacionei.data.repository.ParkingRepository
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import io.github.rlimapro.ondeestacionei.network.config.RetrofitConfig
import io.github.rlimapro.ondeestacionei.network.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ParkingViewModel(application: Application) : AndroidViewModel(application) {
    private val networkMonitor = NetworkMonitor(application)
    private val repository: ParkingRepository
    private val settingsManager = SettingsManager(application)

    val showNotePreference = settingsManager.showNoteDialog.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    private val _uiState = MutableStateFlow(ParkingUiState())

    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).parkingDao()
        repository = ParkingRepository(dao, RetrofitConfig.orsApiService)
        observeLocations()

        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            val addressName = getAddressFromCoords(lat, lng)

            val newLocation = ParkingLocation(
                latitude = lat,
                longitude = lng,
                timestamp = System.currentTimeMillis(),
                address = addressName,
                note = note
            )
            repository.insert(newLocation)
        }
    }

    private fun getAddressFromCoords(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                "${address.thoroughfare ?: "Rua desconhecida"}, ${address.subThoroughfare ?: "S/N"} - ${address.subLocality ?: ""}"
            } else {
                "Endereço não encontrado"
            }
        } catch (e: Exception) {
            "Erro ao obter endereço"
        }
    }

    fun fetchRoute(start: LatLng, end: LatLng) {
        viewModelScope.launch {
            try {
                val mode = _uiState.value.currentMode.profile
                val points = repository.getRoutePoints(start, end, mode)
                _uiState.update { it.copy(routePoints = points) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao calcular rota") }
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

    fun updateTransportMode(mode: TransportMode) {
        _uiState.update { it.copy(currentMode = mode) }
    }

    fun toggleNotePreference(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setShowNoteDialog(enabled) }
    }

    fun updateGpsStatus(enabled: Boolean) {
        _uiState.update { it.copy(isGpsEnabled = enabled) }
    }
}