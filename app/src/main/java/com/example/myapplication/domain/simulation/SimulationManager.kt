package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.usecase.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * THIN SIMULATION BRIDGE.
 * Delegates authoritative logic to specialized Use Cases.
 */
@Singleton
class SimulationManager @Inject constructor(
    private val petRepository: PetRepository,
    private val economyRepository: EconomyRepository,
    private val useItemUseCase: UseItemUseCase,
    private val equipItemUseCase: EquipItemUseCase,
    private val processSimulationTickUseCase: ProcessSimulationTickUseCase,
    private val workHarderUseCase: WorkHarderUseCase,
    private val engine: SimulationEngine,
    val cheatManager: com.example.myapplication.domain.admin.CheatManager,
    val socialManager: com.example.myapplication.domain.social.SocialManager
) {

    suspend fun useItem(itemId: String): Boolean = useItemUseCase(itemId)

    suspend fun equipItem(itemId: String): Boolean = equipItemUseCase(itemId)

    suspend fun processMinigameResult(score: Int) {
        val currentPet = petRepository.getPetState().first() ?: return
        val cost = engine.getMinigameCost()
        val bonusHappiness = (score / 250f).coerceIn(0f, 15f)
        
        val updatedStats = currentPet.stats.copy(
            energy = currentPet.stats.energy + cost.energyChange,
            hunger = currentPet.stats.hunger + cost.hungerChange,
            happiness = (currentPet.stats.happiness + cost.happinessChange + bonusHappiness).coerceIn(0f, 100f)
        ).clamped()
        
        petRepository.savePetState(currentPet.copy(stats = updatedStats))
    }

    suspend fun toggleSleep() {
        val currentPet = petRepository.getPetState().first() ?: return
        val isNowSleeping = !currentPet.isSleeping
        petRepository.savePetState(currentPet.copy(isSleeping = isNowSleeping))
    }

    suspend fun processManualPetting() {
        val currentPet = petRepository.getPetState().first() ?: return
        val updatedStats = currentPet.stats.copy(
            happiness = (currentPet.stats.happiness + 8f).coerceAtMost(100f),
            social = (currentPet.stats.social + 12f).coerceAtMost(100f)
        ).clamped()
        petRepository.savePetState(currentPet.copy(stats = updatedStats))
    }

    suspend fun purchaseItem(item: ItemModel): Boolean {
        val currentPet = petRepository.getPetState().first() ?: return false
        
        if (!item.isConsumable && currentPet.ownedPermanentIds.contains(item.id)) {
            return false
        }

        if (item.isConsumable) {
            economyRepository.addItem(item.id, 1, item.rarity, item.floatValue)
            return true
        } else {
            val newOwned = currentPet.ownedPermanentIds.toMutableSet()
            newOwned.add(item.id)
            petRepository.savePetState(currentPet.copy(ownedPermanentIds = newOwned))
            return true
        }
    }

    suspend fun tickSimulation(currentTimeMillis: Long) {
        processSimulationTickUseCase(currentTimeMillis)
    }

    suspend fun updatePetState(transform: (PetModel) -> PetModel) {
        val currentPet = petRepository.getPetState().first() ?: return
        petRepository.savePetState(transform(currentPet))
    }

    suspend fun workHarder() {
        workHarderUseCase()
    }
}
