package com.example.njttracker.common.domain

data class LineChipState(
    val id: String,
    val name: String,
    val displayName: String,
    val color: String,
    val selected: Boolean = true,
)