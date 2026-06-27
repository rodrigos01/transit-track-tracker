package com.example.njttracker.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils

object LineBadgeDefaults {
    @Composable
    fun textColor(lineColor: Color) = if (ColorUtils.calculateContrast(
            MaterialTheme.colorScheme.onPrimary.toArgb(), lineColor.toArgb()
        ) >= 3
    ) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun LineBadge(lineName: String, lineColor: Color) {
    Text(
        text = lineName,
        style = MaterialTheme.typography.titleSmall,
        color = LineBadgeDefaults.textColor(lineColor),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(lineColor)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    )
}

@Composable
@Preview
fun LineBadgePreview() {
    LineBadge(lineName = "Red", lineColor = Color.Yellow)
}