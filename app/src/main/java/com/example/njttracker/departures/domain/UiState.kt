package com.example.njttracker.departures.domain

import com.example.njttracker.common.domain.DepartureState
import com.example.njttracker.common.domain.LineChipState

data class UiState(
    val title: String,
    val shouldShowBackButton: Boolean = false,
    val lines: List<LineChipState> = emptyList(),
    val departures: List<DepartureState> = emptyList(),
)