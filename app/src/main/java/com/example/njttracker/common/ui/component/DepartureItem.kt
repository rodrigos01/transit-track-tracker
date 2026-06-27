package com.example.njttracker.common.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.njttracker.common.domain.DepartureState
import com.example.njttracker.common.domain.TrainCarState
import com.example.njttracker.common.domain.TrainOccupancyState
import com.example.njttracker.common.domain.TrainStopState
import com.example.njttracker.common.model.TrackConfidence

@Composable
fun DepartureItem(departure: DepartureState, onTapped: () -> Unit, modifier: Modifier = Modifier) {
    val colorInt = departure.lineColor.toColorInt()
    val lineColor = Color(colorInt)
    val cardColor =
        Color(colorInt).copy(alpha = 0.2F).compositeOver(MaterialTheme.colorScheme.surface)

    var expanded by remember { mutableStateOf(false) }

    val horizontalPadding by animateDpAsState(if (expanded) 8.dp else 16.dp)
    val backgroundColor by animateColorAsState(if (expanded) cardColor.copy(alpha = 0.5F) else Color.Transparent)
    val innerPadding by animateDpAsState(if (expanded) 8.dp else 0.dp)

    Column(
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .padding(innerPadding),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onTapped)
                .then(
                    if (expanded) {
                        Modifier.padding(bottom = 8.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            Text(
                text = departure.time,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(72.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(cardColor)
                    .padding(8.dp)
                    .weight(1F)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LineBadge(
                        lineName = departure.lineName, lineColor = lineColor
                    )
                    if (departure.isCompact) {
                        Text(text = departure.destination)
                    } else {
                        Text(
                            text = departure.trainId, style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                if (!departure.isCompact) {
                    Text(text = departure.destination)
                }
            }
            val borderColor = when (departure.trackConfidence) {
                TrackConfidence.NONE -> Color(0x00000000)
                TrackConfidence.LOW -> Color(0xFFAB0000)
                TrackConfidence.MEDIUM -> Color(0xFFAB7D00)
                TrackConfidence.HIGH -> Color(0xFF1B8F10)
                TrackConfidence.CONFIRMED -> MaterialTheme.colorScheme.onSurface
            }
            Text(
                autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = 52.sp),
                text = departure.track,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxHeight()
                    .aspectRatio(1F)
                    .border(
                        2.dp, color = borderColor, shape = MaterialTheme.shapes.medium
                    )
                    .background(
                        MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium
                    )
                    .align(Alignment.CenterVertically)
                    .padding(4.dp)
            )
        }
        val occupancyHeigh by animateDpAsState(if (expanded) 32.dp else 24.dp)
        val cars = departure.occupancy.cars
        if (cars.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(occupancyHeigh)
                    .padding(bottom = 4.dp)
                    .clip(MaterialTheme.shapes.medium),
            ) {
                cars.forEach { car ->
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .background(
                                MaterialTheme.colorScheme.surface
                            )
                    ) {
                        if (car.occupancy > 0) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(
                                    modifier = Modifier.then(
                                        if (car.occupancy < 1) {
                                            Modifier.weight(1F - car.occupancy)
                                        } else {
                                            Modifier
                                        }
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(car.occupancy)
                                        .fillMaxWidth()
                                        .background(lineColor)
                                )
                            }
                        }
                        Text(
                            car.position,
                            color = if (car.occupancy > 0.5F) LineBadgeDefaults.textColor(lineColor) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }
        AnimatedContent(
            departure.stops,
            transitionSpec = {
                slideInVertically().togetherWith(
                    slideOutVertically()
                )
            },
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxWidth(),
        ) { stops ->
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (stops.isNotEmpty()) {
                    stops.forEach { stop ->
                        ListItem(
                            headlineContent = {
                                Text(text = stop.stationName)
                            },
                            trailingContent = {
                                Text(text = stop.time)
                            },
                            modifier = Modifier.clip(MaterialTheme.shapes.large),
                        )
                    }
                    DisposableEffect(stops) {
                        expanded = true
                        onDispose {
                            expanded = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DepartureItemPreview() {
    Surface {
        DepartureItem(
            DepartureState(
                trainId = "Train 1",
                lineName = "Line 1",
                lineColor = "#FF0000",
                destination = "Destination 1",
                time = "12:34 PM",
                track = "1",
                trackConfidence = TrackConfidence.HIGH,
                occupancy = TrainOccupancyState(
                    occupancy = 0.5F, cars = List(7) {
                        TrainCarState(
                            position = it.toString(), occupancy = (1..100).random() / 100F
                        )
                    }),
                stops = List(7) {
                    TrainStopState(
                        stationName = "Station ${it + 1}", time = "12:34 PM"
                    )
                }),
            onTapped = {},
        )
    }
}