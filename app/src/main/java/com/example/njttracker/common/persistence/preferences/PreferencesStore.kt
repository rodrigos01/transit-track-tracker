package com.example.njttracker.common.persistence.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.njttracker.common.data.LocalDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesStore @Inject constructor(@ApplicationContext private val context: Context) : LocalDataStore {

    private val FAVORITE_STATIONS_KEY = stringSetPreferencesKey("favorite_stations")
    private val FAVORITE_LINES_KEY = stringSetPreferencesKey("favorite_lines")

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    override val favoriteStationIds: Flow<Set<String>> = context.dataStore.data.map {
        it[FAVORITE_STATIONS_KEY] ?: emptySet()
    }

    override suspend fun addFavoriteStationId(stationId: String) {
        context.dataStore.updateData { preferences ->
            preferences.update(FAVORITE_STATIONS_KEY) { currentFavorites ->
                (currentFavorites ?: emptySet()) + stationId
            }
        }
    }

    override suspend fun removeFavoriteStationId(stationId: String) {
        context.dataStore.updateData { preferences ->
            preferences.update(FAVORITE_STATIONS_KEY) { currentFavorites ->
                (currentFavorites ?: emptySet()) - stationId
            }
        }
    }

    override val favoriteLineIds: Flow<Set<String>> = context.dataStore.data.map {
        it[FAVORITE_LINES_KEY] ?: emptySet()
    }

    override suspend fun addFavoriteLineId(lineId: String) {
        context.dataStore.updateData { preferences ->
            preferences.update(FAVORITE_LINES_KEY) { currentFavorites ->
                (currentFavorites ?: emptySet()) + lineId
            }
        }
    }

    override suspend fun removeFavoriteLineId(lineId: String) {
        context.dataStore.updateData { preferences ->
            preferences.update(FAVORITE_LINES_KEY) { currentFavorites ->
                (currentFavorites ?: emptySet()) - lineId
            }
        }
    }

    private fun <T> Preferences.update(key: Preferences.Key<T>, transform: (T?) -> T): Preferences {
        return toMutablePreferences().apply {
            set(key, transform(get(key)))
        }
    }
}
