package com.example.njttracker.common.network

import android.util.Log
import com.example.njttracker.common.analytics.AnalyticsLogger
import com.example.njttracker.common.data.RemoteDataStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

//private const val BASE_URL = "https://penn-njt-tracker-api-1082897770533.us-east1.run.app"
private const val BASE_URL = "http://10.0.2.2:3000"

class Api @Inject constructor(private val logger: AnalyticsLogger) : RemoteDataStore {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        install(SSE)
    }

    override fun departures(stationId: String): Flow<DeparturesResponse> = flow {
        client.sse("$BASE_URL/stations/${stationId}/departures/stream") {
            incoming.collect { event ->
                val data = event.data ?: return@collect
                try {
                    val departuresResponse = json.decodeFromString<DeparturesResponse>(data)
                    emit(departuresResponse)
                    Log.d("departures", "received departures chunk: $departuresResponse")
                } catch (e: Exception) {
                    logger.logError("error parsing departures chunk: $data", e)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun stations(favorites: Set<String>?): StationsResponse =
        withContext(Dispatchers.IO) {
            val favoritesQuery = favorites?.joinToString(",") ?: ""
            client.get("$BASE_URL/stations?favorites=$favoritesQuery").body()
        }

    override suspend fun stops(trainId: String): StopsResponse = withContext(Dispatchers.IO) {
        client.get("$BASE_URL/trains/${trainId}/stops").body()
    }
}
