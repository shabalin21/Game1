package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.UpgradeModel
import kotlinx.coroutines.flow.Flow

interface UpgradeRepository {
    fun getUpgrades(): Flow<List<UpgradeModel>>
    suspend fun buyUpgrade(upgradeId: String): Boolean
    suspend fun getUpgradeMultiplier(upgradeId: String): Float
}
