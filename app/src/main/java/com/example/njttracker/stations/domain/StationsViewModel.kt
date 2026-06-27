package com.example.njttracker.stations.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.njttracker.common.domain.LineChipState
import com.example.njttracker.common.domain.asState
import com.example.njttracker.common.navigation.NavDestination
import com.example.njttracker.stations.data.StationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val repository: StationsRepository,
) : ViewModel() {

    val externalState =
        combine(repository.stations, repository.favoriteStations) { result, favoriteIds ->
            Pair(result.getOrDefault(emptyList()), favoriteIds)
        }.filter { it.first.isNotEmpty() }.stateIn(
                viewModelScope, SharingStarted.Lazily, Pair(emptyList(), emptySet())
            )

    fun uiState(intents: Flow<Intent?>): StateFlow<UiState> {
        val loadedState = loadedState(intents)
        val intentFlow = intents.onEach { intent ->
            when (intent) {
                is Intent.StationFavoriteTapped -> onStationFavoriteTapped(intent.station)
                else -> Unit
            }
        }.distinctUntilChanged()
        return combine(loadedState, intentFlow) { state, intent ->
            when (intent) {
                is Intent.StationTapped -> UiState.Navigate(NavDestination.Departures(intent.station.id))
                else -> state
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Loaded(emptyList()))
    }

    private fun loadedState(intents: Flow<Intent?>) =
        intents.combine(externalState) { intent, (stations, favoriteIds) ->
            Triple(intent, stations, favoriteIds)
        }.scan(UiState.Loaded(stations = emptyList())) { state, (intent, stations, favoriteIds) ->
            val filter = when (intent) {
                is Intent.SearchQueryChanged -> intent.query
                is Intent.SearchQueryCleared -> ""
                else -> state.filterQuery
            }
            UiState.Loaded(
                stations = stations.asSequence().filter { station ->
                    station.stationName.contains(filter, ignoreCase = true)
                }.map { station ->
                    StationState(
                        id = station.stationCode,
                        name = station.stationName,
                        displayName = station.stationFull,
                        isWheelChairAccessible = station.wheelchairAccessible,
                        isFavorite = favoriteIds.contains(station.stationCode),
                        lines = station.lines.map {
                            LineChipState(
                                id = it.lineCode,
                                name = it.lineAbbreviation,
                                displayName = it.lineName,
                                color = it.lineColor,
                            )
                        },
                        nextDeparture = station.nextDeparture?.asState(isCompact = true)
                    )
                }.sortedByDescending { it.isFavorite }.toList(),
                filterQuery = filter,
            )
        }

    private fun onStationFavoriteTapped(station: StationState) {
        viewModelScope.launch {
            if (!station.isFavorite) {
                repository.addFavoriteStation(station.id)
            } else {
                repository.removeFavoriteStation(station.id)
            }
        }
    }
}