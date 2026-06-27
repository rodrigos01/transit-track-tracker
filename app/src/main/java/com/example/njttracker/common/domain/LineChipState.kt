package com.example.njttracker.common.domain

data class LineChipState(
    val id: String,
    val name: String,
    val displayName: String,
    val color: String,
    val selected: Boolean = true,
    val highlighted: Boolean = false,
    val isFavorite: Boolean = false,
)