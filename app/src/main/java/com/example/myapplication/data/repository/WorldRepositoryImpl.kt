package com.example.myapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.domain.model.WorldState
import com.example.myapplication.domain.repository.WorldRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "world_settings")

@Singleton
class WorldRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : WorldRepository {

    private val worldKey = stringPreferencesKey("world_state")

    override fun getWorldState(): Flow<WorldState> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[worldKey]
            if (jsonString != null) {
                json.decodeFromString<WorldState>(jsonString)
            } else {
                WorldState()
            }
        }
    }

    override suspend fun updateWorldState(update: (WorldState) -> WorldState) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[worldKey]
            val currentState = if (currentJson != null) {
                json.decodeFromString<WorldState>(currentJson)
            } else {
                WorldState()
            }
            val newState = update(currentState)
            preferences[worldKey] = json.encodeToString(newState)
        }
    }
}
