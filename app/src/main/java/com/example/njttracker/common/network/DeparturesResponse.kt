package com.example.njttracker.common.network

import com.example.njttracker.common.model.Departure
import com.example.njttracker.common.model.Station
import com.example.njttracker.common.model.TrainStop
import kotlinx.serialization.Serializable

@Serializable
data class DeparturesResponse(
    val station: Station, val departures: List<Departure>
)

@Serializable
data class StationsResponse(val stations: List<Station>)

@Serializable
data class StopsResponse(val stops: List<TrainStop>)