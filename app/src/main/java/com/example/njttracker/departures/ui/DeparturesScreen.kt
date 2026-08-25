package com.example.njttracker.departures.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.njttracker.common.domain.DepartureState
import com.example.njttracker.common.domain.LineChipState
import com.example.njttracker.common.domain.TrainOccupancyState
import com.example.njttracker.common.domain.TrainStopState
import com.example.njttracker.common.model.TrackConfidence
import com.example.njttracker.common.model.TrainStatus
import com.example.njttracker.common.ui.component.DepartureItem
import com.example.njttracker.common.ui.component.LineBadgeDefaults
import com.example.njttracker.common.ui.theme.NJTTrackerTheme
import com.example.njttracker.departures.domain.DeparturesViewModel
import com.example.njttracker.departures.domain.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun DeparturesScreen(
    stationId: String, navController: NavController, modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<DeparturesViewModel, DeparturesViewModel.Factory> {
        it.create(navController, stationId)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DeparturesScreen(
        state,
        onBackTapped = viewModel::onBackTapped,
        onLineTapped = viewModel::onLineTapped,
        onDepartureTapped = viewModel::onDepartureTapped,
        onLineFavoriteTapped = viewModel::onLineFavoriteTapped,
        modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DeparturesScreen(
    state: UiState,
    onBackTapped: () -> Unit,
    onLineTapped: (LineChipState) -> Unit,
    onDepartureTapped: (DepartureState) -> Unit,
    onLineFavoriteTapped: (LineChipState) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("${state.title} Departures")
            },
            navigationIcon = {
                IconButton(onClick = onBackTapped) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                }
            },
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = modifier.background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            )
        ) {
            stickyHeader {
                Column {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        items(state.lines, key = { it.id }) {
                            val selectedColor = Color(it.color.toColorInt())
                            val unselectedColor = selectedColor.copy(alpha = 0.2F)
                            val contentColor = LineBadgeDefaults.textColor(selectedColor)
                            FilterChip(
                                selected = it.selected,
                                onClick = { onLineTapped(it) },
                                label = { Text(it.name) },
                                trailingIcon = {
                                    if (it.isFavorite || it.highlighted) {
                                        Icon(
                                            if (it.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = null,
                                            modifier = Modifier.clickable(onClick = {
                                                onLineFavoriteTapped(it)
                                            })
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = selectedColor,
                                    selectedLabelColor = contentColor,
                                    selectedTrailingIconColor = contentColor,
                                    containerColor = unselectedColor,
                                ),
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Time", modifier = Modifier.width(72.dp))
                        Text("Line", modifier = Modifier.weight(1F))
                        Text("Track", modifier = Modifier.width(56.dp))
                    }
                }
            }
            items(state.departures, key = { "${it.time} ${it.trainId}" }) { departure ->
                DepartureItem(
                    departure,
                    onTapped = { onDepartureTapped(departure) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@Composable
@Preview
fun DeparturesScreenPreview() {
    NJTTrackerTheme {
        Scaffold { innerPadding ->
            val lines = listOf(
                "Red", "Blue", "Green", "Yellow", "Orange", "Purple"
            )
            DeparturesScreen(
                modifier = Modifier.padding(innerPadding),
                state = UiState(
                    title = "Station Name",
                    lines = lines.map {
                        LineChipState(
                            id = it,
                            name = it,
                            displayName = it,
                            color = "#FF0000",
                        )
                    },
                    departures = List(12) { departureIndex ->
                        val lineId = ((departureIndex + 1) * 12).toString()
                        val time = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(
                            15L * departureIndex
                        )
                        val lineName = lines.random()
                        val timeString = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                            .format(Date(time))
                        DepartureState(
                            trainId = lineId,
                                       lineName = lineName,
                                       lineColor = "#FF0000",
                                       destination = "${lineName}Town",
                                       scheduledTime = timeString,
                                       time = timeString,
                                       status = TrainStatus.ON_TIME,
                                       track = (1..23).random().toString(),
                                       trackConfidence = TrackConfidence.entries.toTypedArray()
                                           .random(),
                                       occupancy = TrainOccupancyState(
                                           occupancy = (1..100).random() / 100F, cars = emptyList()
                                       ),
                                       stops = if (departureIndex == 2) {
                                           List(7) {
                                               TrainStopState(
                                                   stationName = "Station ${it + 1}",
                                                   time = SimpleDateFormat
                                                       .getTimeInstance(SimpleDateFormat.SHORT)
                                                       .format(
                                                           Date(
                                                               time + TimeUnit.MINUTES.toMillis(
                                                                   7L * it
                                                               )
                                                           )
                                                       )
                                               )
                                           }
                                       } else {
                                           emptyList()
                                       })
                    },
                ),
                onBackTapped = {},
                onLineTapped = {},
                onLineFavoriteTapped = {},
                onDepartureTapped = {},
            )
        }
    }
}