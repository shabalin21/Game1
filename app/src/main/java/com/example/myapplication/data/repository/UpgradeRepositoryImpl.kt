package com.example.myapplication.data.repository

import com.example.myapplication.data.local.PetDao
import com.example.myapplication.data.local.toEntity
import com.example.myapplication.domain.model.UpgradeModel
import com.example.myapplication.domain.model.UpgradeRegistry
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.UpgradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpgradeRepositoryImpl @Inject constructor(
    private val petDao: PetDao,
    private val economyRepository: EconomyRepository
) : UpgradeRepository {

    override fun getUpgrades(): Flow<List<UpgradeModel>> {
        return petDao.getAllUpgrades().map { entities ->
            UpgradeRegistry.allUpgrades.map { base ->
                val entity = entities.find { it.id == base.id }
                base.copy(currentLevel = entity?.level ?: 0)
            }
        }
    }

    override suspend fun buyUpgrade(upgradeId: String): Boolean {
        val base = UpgradeRegistry.allUpgrades.find { it.id == upgradeId } ?: return false
        val entities = petDao.getAllUpgrades().first()
        val currentLevel = entities.find { it.id == upgradeId }?.level ?: 0
        
        val upgrade = base.copy(currentLevel = currentLevel)
        if (upgrade.isMaxLevel()) return false
        
        val cost = upgrade.getNextLevelCost()
        if (economyRepository.spendCoins(cost)) {
            petDao.updateUpgrade(upgrade.copy(currentLevel = currentLevel + 1).toEntity())
            return true
        }
        return false
    }

    override suspend fun getUpgradeMultiplier(upgradeId: String): Float {
        val upgrade = petDao.getUpgradeById(upgradeId)
        val level = upgrade?.level ?: 0
        // Base logic for multipliers: 1.0 + (level * 0.1) for most things
        return 1.0f + (level * 0.1f)
    }
}
