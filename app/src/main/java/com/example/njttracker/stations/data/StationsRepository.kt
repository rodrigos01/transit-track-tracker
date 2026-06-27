package com.example.njttracker.stations.data

import com.example.njttracker.common.data.LocalDataStore
import com.example.njttracker.common.data.RemoteDataStore
import com.example.njttracker.common.model.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StationsRepository @Inject constructor(
    private val remoteDataStore: RemoteDataStore,
    private val localDataStore: LocalDataStore,
) {

    val favoriteStations: Flow<Set<String>> = localDataStore.favoriteStationIds

    val stations: Flow<Result<List<Station>>> = flow {
        val scope = CoroutineScope(currentCoroutineContext())
        val favorites = favoriteStations.stateIn(scope)
        while (true) {
            val stations = runCatching { remoteDataStore.stations(favorites.value).stations }
            emit(stations)
            delay(TimeUnit.SECONDS.toMillis(30))
        }
    }

    suspend fun getStations(): List<Station> {
        val favorites = localDataStore.favoriteStationIds.first()
        return remoteDataStore.stations(favorites).stations
    }

    suspend fun addFavoriteStation(stationId: String) {
        localDataStore.addFavoriteStationId(stationId)
    }

    suspend fun removeFavoriteStation(stationId: String) {
        localDataStore.removeFavoriteStationId(stationId)
    }
}
