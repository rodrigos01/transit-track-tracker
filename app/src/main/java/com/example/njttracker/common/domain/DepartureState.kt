package com.example.njttracker.common.domain

import com.example.njttracker.common.model.Departure
import com.example.njttracker.common.model.TrackConfidence
import java.text.SimpleDateFormat
import java.util.Date

data class DepartureState(
    val trainId: String,
    val lineName: String,
    val lineColor: String,
    val destination: String,
    val time: String,
    val track: String,
    val trackConfidence: TrackConfidence,
    val occupancy: TrainOccupancyState,
    val stops: List<TrainStopState> = emptyList(),
    val isCompact: Boolean = false,
)

data class TrainOccupancyState(
    val occupancy: Float?,
    val cars: List<TrainCarState>,
)

data class TrainCarState(
    val position: String,
    val occupancy: Float,
)

fun Departure.asState(
    stops: List<TrainStopState> = emptyList(), isCompact: Boolean = false
): DepartureState = DepartureState(
    trainId = trainId,
    lineName = lineName,
    lineColor = lineColor,
    destination = destination,
    time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(Date(time)),
    track = track ?: "-",
    trackConfidence = trackConfidence,
    occupancy = TrainOccupancyState(occupancy, cars.map {
        TrainCarState(position = it.position.toString(), occupancy = it.occupancy)
    }),
    stops = stops,
    isCompact = isCompact,
)