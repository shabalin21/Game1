package com.example.myapplication.domain.state

import com.example.myapplication.domain.model.GameState
import com.example.myapplication.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameStateManager @Inject constructor(
    private val petRepository: PetRepository,
    private val inventoryRepository: InventoryRepository,
    private val economyRepository: EconomyRepository,
    private val statisticsRepository: StatisticsRepository,
    private val settingsRepository: SettingsRepository,
    private val upgradeRepository: UpgradeRepository,
    private val worldRepository: WorldRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val gameState: StateFlow<GameState> = combine(
        petRepository.getPetState().catch { e -> Timber.e(e, "Error in petState flow"); emit(null) },
        inventoryRepository.getInventory().catch { e -> Timber.e(e, "Error in inventory flow"); emit(emptyList()) },
        economyRepository.getCoins().catch { e -> Timber.e(e, "Error in coins flow"); emit(0) },
        statisticsRepository.getStatistics().catch { e -> Timber.e(e, "Error in statistics flow"); emit(com.example.myapplication.domain.model.LifetimeStats()) },
        settingsRepository.getSettings().catch { e -> Timber.e(e, "Error in settings flow"); emit(com.example.myapplication.domain.model.SettingsModel()) },
        upgradeRepository.getUpgrades().catch { e -> Timber.e(e, "Error in upgrades flow"); emit(emptyList()) },
        worldRepository.getWorldState().catch { e -> Timber.e(e, "Error in worldState flow"); emit(com.example.myapplication.domain.model.WorldState()) }
    ) { flows ->
        try {
            GameState(
                pet = flows[0] as com.example.myapplication.domain.model.PetModel?,
                inventory = flows[1] as List<InventoryItem>,
                coins = flows[2] as Int,
                statistics = flows[3] as com.example.myapplication.domain.model.LifetimeStats,
                settings = flows[4] as com.example.myapplication.domain.model.SettingsModel,
                upgrades = flows[5] as List<com.example.myapplication.domain.model.UpgradeModel>,
                world = flows[6] as com.example.myapplication.domain.model.WorldState
            )
        } catch (e: Exception) {
            Timber.e(e, "Critical error mapping GameState")
            GameState()
        }
    }
    .stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = GameState()
    )

    init {
        Timber.i("GameStateManager initialized")
    }
}
