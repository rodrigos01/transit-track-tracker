package com.example.njttracker.departures.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.njttracker.common.domain.DepartureState
import com.example.njttracker.common.domain.LineChipState
import com.example.njttracker.common.domain.TrainStopState
import com.example.njttracker.common.domain.asState
import com.example.njttracker.departures.data.DeparturesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@HiltViewModel(assistedFactory = DeparturesViewModel.Factory::class)
class DeparturesViewModel @AssistedInject constructor(
    private val repository: DeparturesRepository,
    @Assisted private val navController: NavController,
    @Assisted private val stationId: String
) : ViewModel() {

    private data class InputState(
        val selectedLines: Set<String> = emptySet(),
        val expandedTrain: String? = null,
        val expandedTrainStops: List<TrainStopState> = emptyList(),
    )

    private val trainStops = mutableMapOf<String, List<TrainStopState>>()

    private val shouldShowBackButton = navController.previousBackStackEntry != null

    private val inputState = MutableStateFlow(InputState())

    val uiState: StateFlow<UiState> =
        combine(inputState, repository.getDepartures(stationId)) { input, stationResult ->
            val stationDetails = stationResult.getOrNull()
            if (stationDetails != null) {
                val station = stationDetails.station
                UiState(
                    title = station.stationFull,
                    shouldShowBackButton = shouldShowBackButton,
                    lines = station.lines.map {
                        LineChipState(
                            id = it.lineCode,
                            name = it.lineAbbreviation,
                            displayName = it.lineName,
                            color = it.lineColor,
                            selected = input.selectedLines.contains(it.lineCode) || input.selectedLines.isEmpty(),
                        )
                    }.sortedByDescending { it.selected },
                    departures = stationDetails.departures.asSequence().filter { departure ->
                        input.selectedLines.isEmpty() || input.selectedLines.contains(departure.lineCode)
                    }.map { departure ->
                        departure.asState(
                            stops = input.expandedTrainStops
                                .takeIf { input.expandedTrain == departure.trainId }.orEmpty()
                        )
                    }.toList(),
                )
            } else {
                UiState(title = "Error retrieving departures")
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState(title = "", shouldShowBackButton = shouldShowBackButton)
        )

    fun onLineTapped(line: LineChipState) {
        val selected = inputState.value.selectedLines.contains(line.id)
        inputState.value = inputState.value.copy(
            selectedLines = if (!selected) {
                inputState.value.selectedLines + line.id
            } else {
                inputState.value.selectedLines - line.id
            }
        )
    }

    fun onDepartureTapped(departure: DepartureState) {
        if (inputState.value.expandedTrain == departure.trainId) {
            inputState.value = inputState.value.copy(
                expandedTrain = null,
                expandedTrainStops = emptyList(),
            )
        } else {
            viewModelScope.launch {
                val stops = trainStops.getOrPut(departure.trainId) {
                    val stopsData = repository.getStops(departure.trainId)
                    val departureIndex = stopsData.indexOfFirst { it.stationCode == stationId }
                    repository.getStops(departure.trainId).asSequence()
                        .filterIndexed { index, _ -> index > departureIndex }.map {
                            TrainStopState(
                                stationName = it.stationName,
                                time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                                    .format(Date(it.arrivalTime))
                            )
                        }.toList()
                }
                inputState.value = inputState.value.copy(
                    expandedTrain = departure.trainId,
                    expandedTrainStops = stops,
                )
            }
        }
    }

    fun onBackTapped() {
        navController.popBackStack()
    }

    @AssistedFactory
    interface Factory {
        fun create(navController: NavController, stationId: String): DeparturesViewModel
    }

}