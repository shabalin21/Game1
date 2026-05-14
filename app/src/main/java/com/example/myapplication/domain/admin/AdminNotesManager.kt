package com.example.myapplication.domain.admin

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "admin_notes")

@Singleton
class AdminNotesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val NOTES_KEY = stringPreferencesKey("dev_notes")

    val notes: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[NOTES_KEY] ?: ""
    }

    suspend fun saveNotes(text: String) {
        context.dataStore.edit { preferences ->
            preferences[NOTES_KEY] = text
        }
    }
}
