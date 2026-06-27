package com.example.njttracker.stations.domain

sealed interface Intent {
    data class SearchQueryChanged(val query: String) : Intent
    data object SearchQueryCleared : Intent
    data class StationTapped(val station: StationState) : Intent
    data class StationFavoriteTapped(val station: StationState) : Intent
}