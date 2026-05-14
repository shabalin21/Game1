package com.example.myapplication.domain.progression

import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.StatisticsRepository
import com.example.myapplication.domain.model.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrestigeManager @Inject constructor(
    private val petRepository: PetRepository,
    private val economyRepository: EconomyRepository,
    private val statisticsRepository: StatisticsRepository
) {
    suspend fun canPrestige(): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        val coins = economyRepository.getCoins().first()
        // Requirements: Level 100 OR 10 Million Credits
        return pet.level >= 100 || coins >= 10000000
    }

    suspend fun performRebirth(): Boolean {
        if (!canPrestige()) return false
        
        val currentStats = statisticsRepository.getStatistics().first()
        val pet = petRepository.getPetState().first() ?: return false
        
        val newMultiplier = currentStats.prestigeMultiplier + 0.5f
        val newRebirthCount = currentStats.rebirthCount + 1
        
        Timber.i("Prestige: Resetting life for rebirth #$newRebirthCount with x$newMultiplier bonus")

        // 1. Reset Economy & Assets
        economyRepository.addCoins(-10000000) // If they had that much
        // Clear permanent assets except Mythics
        val keptAssets = pet.ownedPermanentIds.filter { id ->
            ItemRegistry.getItem(id)?.rarity == ItemRarity.MYTHIC
        }.toSet()
        
        // 2. Reset Pet Stats & Level
        val rebornPet = pet.copy(
            level = 1,
            xp = 0,
            ownedPermanentIds = keptAssets,
            stats = PetStats(), // Reset to defaults
            employment = EmploymentState() // Fire from job
        )
        petRepository.savePetState(rebornPet)

        // 3. Update Global Multipliers
        statisticsRepository.updatePrestige(newMultiplier, newRebirthCount)
        
        return true
    }
}
