package com.example.njttracker.stations.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
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
import com.example.njttracker.common.model.TrackConfidence
import com.example.njttracker.common.navigation.NavDestination
import com.example.njttracker.common.ui.AccessibleForward
import com.example.njttracker.common.ui.Train
import com.example.njttracker.common.ui.component.DepartureItem
import com.example.njttracker.common.ui.component.LineBadge
import com.example.njttracker.common.ui.theme.NJTTrackerTheme
import com.example.njttracker.stations.domain.Intent
import com.example.njttracker.stations.domain.StationState
import com.example.njttracker.stations.domain.StationsViewModel
import com.example.njttracker.stations.domain.UiState

@Composable
fun StationsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val viewModel: StationsViewModel = hiltViewModel<StationsViewModel>()

    val intent = remember { mutableStateOf<Intent?>(null) }
    val state by viewModel.uiState(snapshotFlow { intent.value }).collectAsStateWithLifecycle()
    LaunchedEffect(state) {
        intent.value = null
    }

    StationsScreen(
        state = state,
        onIntent = { intent.value = it },
        onNavigate = { navController.navigate(it) },
        modifier = modifier,
        contentPadding = contentPadding,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(
    state: UiState,
    onIntent: (Intent) -> Unit,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (state) {
        is UiState.Loaded -> StationsList(
            modifier,
            contentPadding,
            state,
            onIntent,
        )

        is UiState.Navigate -> onNavigate(state.destinations)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun StationsList(
    modifier: Modifier,
    contentPadding: PaddingValues,
    state: UiState.Loaded,
    onIntent: (Intent) -> Unit
) {
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = contentPadding.calculateTopPadding(),
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding(),
        )
    ) {
        stickyHeader {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.filterQuery,
                        onQueryChange = { onIntent(Intent.SearchQueryChanged(it)) },
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        placeholder = { Text(text = "Search") },
                        trailingIcon = {
                            if (state.filterQuery.isNotEmpty()) {
                                IconButton(onClick = { onIntent(Intent.SearchQueryCleared) }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        colors = SearchBarDefaults.inputFieldColors(),
                    )
                },
                expanded = false,
                onExpandedChange = {},
                windowInsets = WindowInsets(0, 0, 0, 0),
                content = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
        items(state.stations, key = { it.id }) { station ->
            Column(modifier = Modifier.animateItem()) {
                ListItem(modifier = Modifier
                    .clickable { onIntent(Intent.StationTapped(station)) }
                    .animateItem(), leadingContent = {
                    Icon(
                        Icons.Outlined.Train, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraLarge,
                            )
                            .padding(8.dp),
                    )
                }, headlineContent = {
                    Text(text = station.displayName)
                }, supportingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(state = rememberScrollState())
                    ) {
                        station.lines.forEach { line ->
                            LineBadge(line.name, Color(line.color.toColorInt()))
                        }
                    }
                }, trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        modifier = Modifier.width(72.dp)
                    ) {
                        if (station.isWheelChairAccessible) {
                            Icon(
                                Icons.Rounded.AccessibleForward,
                                contentDescription = "Wheelchair Accessible"
                            )
                        }
                        FilledIconToggleButton(
                            checked = station.isFavorite, onCheckedChange = {
                                onIntent(Intent.StationFavoriteTapped(station))
                            }) {
                            Icon(
                                if (station.isFavorite) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                }, contentDescription = null
                            )
                        }
                    }
                })
                if (station.nextDeparture != null) {
                    Text(
                        text = "Next Departure",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    DepartureItem(
                        departure = station.nextDeparture,
                        onTapped = {},
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showSystemUi = true)
fun StationsScreenPreview() {
    NJTTrackerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Stations")
                    })
            }) { padding ->
            val state = UiState.Loaded(
                stations = List(10) { station ->
                    StationState(
                        id = station.toString(),
                        name = "Station $station",
                        displayName = "Station $station",
                        isWheelChairAccessible = station % 2 == 0,
                        isFavorite = station < 2,
                        lines = List((1..6).random()) {
                            val color = (0x111111..0xFFFFFF).random().toString(16)
                            LineChipState(
                                id = it.toString(),
                                name = "Line $station$it",
                                displayName = "Line $it",
                                color = "#$color",
                            )
                        },
                        nextDeparture = if (station < 2) {
                            DepartureState(
                                trainId = "nextTrain",
                                lineName = "Line $station",
                                lineColor = "#FF0000",
                                destination = "Destination $station",
                                time = "12:34",
                                track = "1",
                                trackConfidence = TrackConfidence.HIGH,
                                occupancy = com.example.njttracker.common.domain.TrainOccupancyState(
                                    occupancy = 0.5F,
                                    cars = emptyList(),
                                ),
                                isCompact = true,
                            )
                        } else {
                            null
                        }
                    )
                },
            )

            StationsScreen(
                state,
                onIntent = {},
                onNavigate = {},
                modifier = Modifier.padding(padding),
            )
        }
    }
}