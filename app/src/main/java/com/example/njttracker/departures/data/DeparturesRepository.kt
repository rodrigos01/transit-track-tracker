package com.example.njttracker.departures.data

import com.example.njttracker.common.data.RemoteDataStore
import com.example.njttracker.common.model.StationDetails
import com.example.njttracker.common.model.TrainStop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeparturesRepository @Inject constructor(private val remoteDataStore: RemoteDataStore) {

    fun getDepartures(stationId: String): Flow<Result<StationDetails>> {
        return remoteDataStore.departures(stationId)
            .map { Result.success(StationDetails(it.station, it.departures)) }
            .catch { emit(Result.failure(it)) }
    }

    suspend fun getStops(trainId: String): List<TrainStop> {
        return remoteDataStore.stops(trainId).stops
    }

}