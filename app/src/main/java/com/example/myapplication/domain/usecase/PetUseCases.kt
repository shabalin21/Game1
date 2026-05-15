package com.example.myapplication.domain.usecase

import com.example.myapplication.core.modifier.Modifier
import com.example.myapplication.core.modifier.ModifierSource
import com.example.myapplication.core.modifier.ModifierType
import com.example.myapplication.domain.admin.CheatManager
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.*
import com.example.myapplication.domain.simulation.SimulationEngine
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseItemUseCase @Inject constructor(
    private val petRepository: PetRepository,
    private val economyRepository: EconomyRepository,
    private val engine: SimulationEngine
) {
    suspend operator fun invoke(itemId: String): Boolean {
        val item = ItemRegistry.getItem(itemId) ?: return false
        val currentPet = petRepository.getPetState().first() ?: return false
        
        if (item.isConsumable) {
            val inventory = economyRepository.getInventory().first()
            val entry = inventory.find { it.itemId == itemId } ?: return false
            
            val success = economyRepository.removeItem(entry.id, 1)
            if (success) {
                val updatedStats = engine.applyItemEffect(currentPet.stats, item)
                
                val newModifiers = currentPet.activeModifiers.toMutableList()
                if (item.durationMillis > 0) {
                    newModifiers.add(
                        TimedModifier(
                            id = "${item.id}_${System.currentTimeMillis()}",
                            name = item.name,
                            effect = item.effect,
                            expirationTimestamp = System.currentTimeMillis() + item.durationMillis,
                            icon = item.icon,
                            sideEffect = item.sideEffect
                        )
                    )
                }

                petRepository.savePetState(currentPet.copy(stats = updatedStats, activeModifiers = newModifiers))
                return true
            }
        }
        return false
    }
}

@Singleton
class EquipItemUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(itemId: String): Boolean {
        val item = ItemRegistry.getItem(itemId) ?: return false
        val currentPet = petRepository.getPetState().first() ?: return false
        
        if (!currentPet.ownedPermanentIds.contains(itemId)) {
            return false
        }

        val newEquipped = currentPet.equippedItems.toMutableMap()
        newEquipped[item.category] = itemId
        
        petRepository.savePetState(currentPet.copy(equippedItems = newEquipped))
        return true
    }
}

@Singleton
class ProcessSimulationTickUseCase @Inject constructor(
    private val petRepository: PetRepository,
    private val worldRepository: WorldRepository,
    private val upgradeRepository: UpgradeRepository,
    private val engine: SimulationEngine,
    private val cheatManager: CheatManager
) {
    suspend operator fun invoke(currentTimeMillis: Long) {
        if (cheatManager.simulationPaused.value) return

        val pet = petRepository.getPetState().first() ?: return
        val world = worldRepository.getWorldState().first()
        val upgrades = upgradeRepository.getUpgrades().first()
        
        // Convert Upgrades to Systemic Modifiers
        val upgradeModifiers = upgrades.filter { it.currentLevel > 0 }.map { up ->
            val value = when(up.id) {
                "energy_decay", "hunger_decay", "happiness_decay", "sleep_quality" -> 1.0f / (1.0f + up.currentLevel * 0.1f)
                else -> 1.0f + up.currentLevel * 0.1f
            }
            Modifier(
                id = up.id,
                name = up.name,
                value = value,
                type = ModifierType.MULTIPLICATIVE,
                source = ModifierSource.UPGRADE,
                tag = up.id
            )
        }
        
        val timeDilation = cheatManager.timeDilation.value
        val effectiveTime = if (timeDilation != 1.0f) {
            val delta = currentTimeMillis - pet.lastUpdateTimestamp
            pet.lastUpdateTimestamp + (delta * timeDilation).toLong()
        } else {
            currentTimeMillis
        }

        val updatedPet = engine.updateState(pet, world, effectiveTime, upgradeModifiers)
        
        val finalStats = if (cheatManager.godModeEnabled.value) {
            updatedPet.stats.copy(hunger = 100f, energy = 100f, happiness = 100f, health = 100f, hygiene = 100f)
        } else {
            var overridden = updatedPet.stats
            cheatManager.frozenStats.value.forEach { (statName, value) ->
                overridden = when (statName.lowercase()) {
                    "hunger" -> overridden.copy(hunger = value)
                    "energy" -> overridden.copy(energy = value)
                    "happiness" -> overridden.copy(happiness = value)
                    "health" -> overridden.copy(health = value)
                    "hygiene" -> overridden.copy(hygiene = value)
                    "social" -> overridden.copy(social = value)
                    "stress" -> overridden.copy(stress = value)
                    else -> overridden
                }
            }
            overridden
        }

        petRepository.savePetState(updatedPet.copy(stats = finalStats))
    }
}

@Singleton
class WorkHarderUseCase @Inject constructor(
    private val petRepository: PetRepository,
    private val engine: SimulationEngine
) {
    suspend operator fun invoke() {
        val currentPet = petRepository.getPetState().first() ?: return
        val updatedPet = engine.applyWorkHarderEffect(currentPet)
        petRepository.savePetState(updatedPet)
    }
}
