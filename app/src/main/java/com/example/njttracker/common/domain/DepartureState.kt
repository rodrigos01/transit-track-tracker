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
    val scheduledTime: String,
    val time: String,
    val isDelayed: Boolean,
    val track: String,
    val trackConfidence: TrackConfidence,
    val occupancy: TrainOccupancyState,
    val stops: List<TrainStopState> = emptyList(),
    val stopsLoading: Boolean = false,
    val isCompact: Boolean = false,
)

data class TrainOccupancyState(
    val occupancy: Float?,
    val cars: List<TrainCarState>,
)

data class TrainCarState(
    val carId: String,
    val position: String,
    val occupancy: Float,
)

fun Departure.asState(
    stops: List<TrainStopState> = emptyList(), isCompact: Boolean = false,
    stopsLoading: Boolean = false,
): DepartureState = DepartureState(
    trainId = trainId,
    lineName = lineName,
    lineColor = lineColor,
    destination = destination,
    scheduledTime = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(Date(time)),
    time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(Date(time)),
    isDelayed = delay > 0,
    track = track ?: "-",
    trackConfidence = trackConfidence,
    occupancy = TrainOccupancyState(occupancy, cars.map {
        TrainCarState(carId = it.carNo, position = it.position.toString(), occupancy = it.occupancy)
    }),
    stops = stops,
    stopsLoading = stopsLoading,
    isCompact = isCompact,
)