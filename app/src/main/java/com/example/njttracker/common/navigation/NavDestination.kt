package com.example.njttracker.common.navigation

import kotlinx.serialization.Serializable

sealed interface NavDestination {

    @Serializable
    object Home: NavDestination
    @Serializable
    data class Departures(val stationId: String): NavDestination
}
