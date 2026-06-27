package com.example.njttracker.stations.domain

import com.example.njttracker.common.domain.DepartureState
import com.example.njttracker.common.domain.LineChipState
import com.example.njttracker.common.navigation.NavDestination

sealed interface UiState {

    data class Navigate(val destinations: NavDestination): UiState

    data class Loaded(
        val stations: List<StationState>,
        val filterQuery: String = "",
    ): UiState {
    }


}

data class StationState(
    val id: String,
    val name: String,
    val displayName: String,
    val isWheelChairAccessible: Boolean,
    val isFavorite: Boolean,
    val lines: List<LineChipState>,
    val nextDeparture: DepartureState? = null,
)

