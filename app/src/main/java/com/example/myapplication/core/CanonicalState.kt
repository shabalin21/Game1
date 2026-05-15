package com.example.myapplication.core

import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The single source of truth for the entire game state.
 * Refactored from GameStateManager to ensure atomicity and kernel integration.
 */
@Singleton
class CanonicalState @Inject constructor(
    private val petRepository: PetRepository,
    private val inventoryRepository: InventoryRepository,
    private val economyRepository: EconomyRepository,
    private val statisticsRepository: StatisticsRepository,
    private val settingsRepository: SettingsRepository,
    private val upgradeRepository: UpgradeRepository,
    private val worldRepository: WorldRepository
) : KernelSystem {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    override fun onBoot() {
        Timber.i("CanonicalState: Booting...")
        
        // Combine all repository flows into a single unified stream
        combine(
            petRepository.getPetState().catch { e -> Timber.e(e, "Error in petState flow"); emit(null) },
            inventoryRepository.getInventory().catch { e -> Timber.e(e, "Error in inventory flow"); emit(emptyList()) },
            economyRepository.getCoins().catch { e -> Timber.e(e, "Error in coins flow"); emit(0) },
            statisticsRepository.getStatistics().catch { e -> Timber.e(e, "Error in statistics flow"); emit(LifetimeStats()) },
            settingsRepository.getSettings().catch { e -> Timber.e(e, "Error in settings flow"); emit(SettingsModel()) },
            upgradeRepository.getUpgrades().catch { e -> Timber.e(e, "Error in upgrades flow"); emit(emptyList()) },
            worldRepository.getWorldState().catch { e -> Timber.e(e, "Error in worldState flow"); emit(WorldState()) }
        ) { flows ->
            try {
                GameState(
                    pet = flows[0] as PetModel?,
                    inventory = flows[1] as List<InventoryItem>,
                    coins = flows[2] as Int,
                    statistics = flows[3] as LifetimeStats,
                    settings = flows[4] as SettingsModel,
                    upgrades = flows[5] as List<UpgradeModel>,
                    world = flows[6] as WorldState
                )
            } catch (e: Exception) {
                Timber.e(e, "Critical error mapping GameState")
                GameState()
            }
        }
        .onEach { newState -> _state.value = newState }
        .launchIn(scope)
    }

    override fun onShutdown() {
        Timber.i("CanonicalState: Shutting down...")
    }
}
