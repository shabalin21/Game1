package com.example.myapplication.data.repository

import com.example.myapplication.data.local.PetDao
import com.example.myapplication.data.local.toDomain
import com.example.myapplication.data.local.toEntity
import com.example.myapplication.domain.model.SettingsModel
import com.example.myapplication.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val petDao: PetDao,
    private val json: Json
) : SettingsRepository {

    override fun getSettings(): Flow<SettingsModel> {
        return petDao.getSettings().map { it?.toDomain(json) ?: SettingsModel() }
    }

    override suspend fun updateSettings(settings: SettingsModel) {
        petDao.updateSettings(settings.toEntity(json))
    }

    override suspend fun resetSettings() {
        petDao.updateSettings(SettingsModel().toEntity(json))
    }
}
