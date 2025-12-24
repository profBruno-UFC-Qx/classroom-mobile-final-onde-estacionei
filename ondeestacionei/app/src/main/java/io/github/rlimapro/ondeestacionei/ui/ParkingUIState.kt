package io.github.rlimapro.ondeestacionei.ui

import io.github.rlimapro.ondeestacionei.model.ParkingLocation

data class ParkingUiState(
    val locations: List<ParkingLocation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastLocation: ParkingLocation? = null
)