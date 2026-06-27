package com.example.njttracker.common.data

import kotlinx.coroutines.flow.Flow

interface LocalDataStore {
    val favoriteStationIds: Flow<Set<String>>
    suspend fun addFavoriteStationId(stationId: String)
    suspend fun removeFavoriteStationId(stationId: String)

    val favoriteLineIds: Flow<Set<String>>
    suspend fun addFavoriteLineId(lineId: String)
    suspend fun removeFavoriteLineId(lineId: String)
}