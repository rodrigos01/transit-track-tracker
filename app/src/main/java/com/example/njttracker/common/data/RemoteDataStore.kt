package com.example.njttracker.common.data

import com.example.njttracker.common.model.TrainStop
import com.example.njttracker.common.network.DeparturesResponse
import com.example.njttracker.common.network.StationsResponse
import com.example.njttracker.common.network.StopsResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataStore {
    fun departures(stationId: String): Flow<DeparturesResponse>
    suspend fun stations(favorites: Set<String>? = null): StationsResponse
    suspend fun stops(trainId: String): StopsResponse
}