package com.example.njttracker.departures.data

import com.example.njttracker.common.data.LocalDataStore
import com.example.njttracker.common.data.RemoteDataStore
import com.example.njttracker.common.model.StationDetails
import com.example.njttracker.common.model.TrainStop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeparturesRepository @Inject constructor(
    private val remoteDataStore: RemoteDataStore,
    private val localDataStore: LocalDataStore
) {

    val favoriteLineIds: Flow<Set<String>> = localDataStore.favoriteLineIds

    fun getDepartures(stationId: String): Flow<Result<StationDetails>> {
        return remoteDataStore.departures(stationId)
            .map { Result.success(StationDetails(it.station, it.departures)) }
            .catch { emit(Result.failure(it)) }
    }

    suspend fun getStops(trainId: String): List<TrainStop> {
        return remoteDataStore.stops(trainId).stops
    }

    suspend fun addFavoriteLine(lineId: String) {
        localDataStore.addFavoriteLineId(lineId)
    }

    suspend fun removeFavoriteLine(lineId: String) {
        localDataStore.removeFavoriteLineId(lineId)
    }
}
