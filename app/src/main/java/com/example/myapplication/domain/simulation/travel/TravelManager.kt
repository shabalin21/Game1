package com.example.myapplication.domain.simulation.travel

import com.example.myapplication.core.EventBus
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.*
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class TravelManager @Inject constructor(
    private val worldRepository: WorldRepository,
    private val petRepository: PetRepository,
    private val economyRepository: EconomyRepository,
    private val logManager: TerminalLogManager
) {
    suspend fun travelTo(targetDistrict: District): Boolean {
        val world = worldRepository.getWorldState().first()
        val pet = petRepository.getPetState().first() ?: return false
        
        if (world.currentDistrict == targetDistrict) return true

        val travelInfo = TravelRegistry.getTravelInfo(world.currentDistrict, targetDistrict)
        
        // Check costs
        val coins = economyRepository.getCoins().first()
        if (coins < travelInfo.baseCost) {
            logManager.log(LogCategory.WARNING, "TRAVEL_DENIED: Insufficient funds.")
            return false
        }

        if (pet.stats.energy < travelInfo.energyCost) {
            logManager.log(LogCategory.WARNING, "TRAVEL_DENIED: Insufficient energy.")
            return false
        }

        // Apply costs
        val success = economyRepository.spendCoins(travelInfo.baseCost)
        if (!success) return false
        
        val updatedStats = pet.stats.copy(
            energy = (pet.stats.energy - travelInfo.energyCost).coerceAtLeast(0f),
            stress = (pet.stats.stress + travelInfo.stressGain).coerceAtMost(100f)
        )
        
        // Advance time
        val travelMillis = (travelInfo.durationHours * TimeUnit.HOURS.toMillis(1)).toLong()
        
        worldRepository.updateWorldState { 
            it.copy(
                currentDistrict = targetDistrict,
                lastUpdateTimestamp = it.lastUpdateTimestamp + travelMillis
            )
        }

        petRepository.savePetState(pet.copy(
            stats = updatedStats.clamped(),
            lastUpdateTimestamp = pet.lastUpdateTimestamp + travelMillis
        ))

        logManager.log(LogCategory.SYSTEM, "Arrived at $targetDistrict. Travel took ${travelInfo.durationHours}h.")
        return true
    }
}
