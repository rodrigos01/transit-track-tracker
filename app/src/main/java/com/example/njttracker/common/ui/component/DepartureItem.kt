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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.njttracker.common.domain.DepartureState
import com.example.njttracker.common.domain.TrainCarState
import com.example.njttracker.common.domain.TrainOccupancyState
import com.example.njttracker.common.domain.TrainStopState
import com.example.njttracker.common.model.TrackConfidence
import com.example.njttracker.common.model.TrainStatus

@Composable
fun DepartureItem(
    departure: DepartureState,
    onTapped: () -> Unit,
    modifier: Modifier = Modifier,
    isExpandable: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    DepartureItem(
        departure = departure,
        expanded = isExpandable && expanded,
        onTapped = {
            expanded = !expanded
            onTapped()
        },
        modifier,
    )
}

@Composable
fun DepartureItem(
    departure: DepartureState,
    expanded: Boolean,
    onTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCancelled = departure.status == TrainStatus.CANCELLED
    val textDecoration =
        if (isCancelled) TextDecoration.LineThrough else null
    val isCompact = departure.isCompact || isCancelled
    val shouldExpand = expanded && !isCancelled

    val colorInt = departure.lineColor.toColorInt()
    val lineColorSoft = Color(colorInt).copy(alpha = 0.2F).compositeOver(MaterialTheme.colorScheme.surface)
    val lineColor = if (isCancelled) lineColorSoft else Color(colorInt)
    val cardColor = if (isCancelled) Color.LightGray else lineColorSoft

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
                    if (shouldExpand) {
                        Modifier.padding(bottom = 8.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(72.dp)
            ) {
                if (departure.status == TrainStatus.DELAYED) {
                    Text(
                        text = departure.scheduledTime,
                        style = MaterialTheme.typography.labelLarge,
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Text(
                    text = departure.time,
                    style = MaterialTheme.typography.labelLarge,
                    textDecoration = textDecoration,
                    modifier = Modifier.width(72.dp)
                )
                if (departure.status == TrainStatus.CANCELLED) {
                    Text(
                        text = "Cancelled",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
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
                        lineColor = lineColor,
                        isFavorite = departure.isFavoriteLine,
                    ) {
                        Text(departure.lineName, textDecoration = textDecoration)
                    }
                    if (isCompact) {
                        Text(text = departure.destination, textDecoration = textDecoration)
                    } else {
                        Text(
                            text = departure.trainId, style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                if (!isCompact) {
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
                textDecoration = textDecoration,
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
        AnimatedContent(
            shouldExpand,
            transitionSpec = {
                slideInVertically().togetherWith(
                    slideOutVertically()
                )
            },
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxWidth(),
        ) { expand ->
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (expand) {
                    val cars = departure.occupancy.cars
                    if (cars.isNotEmpty()) {
                        Text(
                            text = "Occupancy",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(MaterialTheme.shapes.medium),
                        ) {
                            cars.forEach { car ->
                                Box(
                                    modifier = Modifier
                                        .weight(1F)
                                        .fillMaxHeight()
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
                                        car.carId,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (car.occupancy > 0.5F) LineBadgeDefaults.textColor(
                                            lineColor
                                        ) else MaterialTheme.colorScheme.onSurface,
                                        autoSize = TextAutoSize.StepBased(
                                            minFontSize = 4.sp,
                                            maxFontSize = MaterialTheme.typography.labelSmall.fontSize
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 2.dp),
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Front of the train →",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .align(Alignment.End),
                        )
                    }
                    Text(
                        text = "Stops",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (departure.stopsLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally)
                                .animateEnterExit(),
                            color = lineColor,
                        )
                    }
                    departure.stops.forEach { stop ->
                        ListItem(
                            headlineContent = {
                                Text(text = stop.stationName)
                            },
                            trailingContent = {
                                Text(text = stop.time)
                            },
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.large)
                                .animateEnterExit(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DepartureItemPreviewExpanded() {
    DepartureItemPreview(expanded = true)
}

@Composable
@Preview
fun DepartureItemPreview(expanded: Boolean = false, status: TrainStatus = TrainStatus.ON_TIME) {
    Surface {
        DepartureItem(
            DepartureState(
                trainId = "Train 1",
                lineName = "Line 1",
                lineColor = "#FF0000",
                destination = "Destination 1",
                time = "12:38 PM",
                scheduledTime = "12:34 PM",
                status = status,
                track = "1",
                trackConfidence = TrackConfidence.HIGH,
                occupancy = TrainOccupancyState(
                    occupancy = 0.5F, cars = List(14) {
                        TrainCarState(
                            carId = "00$it",
                            position = it.toString(),
                            occupancy = (1..100).random() / 100F
                        )
                    }),
                stops = List(7) {
                    TrainStopState(
                        stationName = "Station ${it + 1}", time = "12:34 PM"
                    )
                },
                stopsLoading = true,
            ),
            expanded = expanded,
            onTapped = {},
        )
    }
}

@Composable
@Preview
fun DepartureItemPreviewCancelled() {
    DepartureItemPreview(status = TrainStatus.CANCELLED)
}