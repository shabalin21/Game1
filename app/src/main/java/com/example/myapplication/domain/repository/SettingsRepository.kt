package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.SettingsModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<SettingsModel>
    suspend fun updateSettings(settings: SettingsModel)
    suspend fun resetSettings()
}
