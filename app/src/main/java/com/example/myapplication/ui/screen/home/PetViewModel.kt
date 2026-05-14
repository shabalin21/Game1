package com.example.myapplication.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.interaction.PetInteractionSystem
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.InventoryRepository
import com.example.myapplication.domain.state.GameStateManager
import com.example.myapplication.domain.usecase.ProcessSimulationTickUseCase
import com.example.myapplication.domain.usecase.WorkHarderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodInventoryItem(
    val item: ItemModel,
    val quantity: Int
)

@HiltViewModel
class PetViewModel @Inject constructor(
    val interactionSystem: PetInteractionSystem,
    private val inventoryRepository: InventoryRepository,
    private val economyRepository: EconomyRepository,
    private val gameStateManager: GameStateManager,
    private val processSimulationTickUseCase: ProcessSimulationTickUseCase,
    private val workHarderUseCase: WorkHarderUseCase,
    private val marketEngine: com.example.myapplication.domain.market.MarketSimulationEngine,
    private val socialManager: com.example.myapplication.domain.social.SocialManager
) : ViewModel() {

    val gameState = gameStateManager.gameState

    val btcPrice = marketEngine.assets.map { list ->
        list.find { it.id == "BTC" }?.currentPrice ?: 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val petState = gameState.map { it.pet }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val worldState = gameState.map { it.world }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorldState())

    val coins = gameState.map { it.coins }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val statistics = gameState.map { it.statistics }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LifetimeStats())

    val settings = gameState.map { it.settings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsModel())

    val trendingPost = flow {
        while(true) {
            emit(socialManager.getTrendingPost())
            kotlinx.coroutines.delay(10000) // Refresh every 10s
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Connecting to Metropolis Grid...")

    // Filtered food items for the Home screen selection
    val foodInventory = inventoryRepository.getInventory().map { inv ->
        inv.mapNotNull { inventoryItem ->
            val item = ItemRegistry.getItem(inventoryItem.itemId)
            if (item != null && (item.category == ItemCategory.PRODUCTS || item.category == ItemCategory.FOOD) && inventoryItem.quantity > 0) {
                FoodInventoryItem(item, inventoryItem.quantity)
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun feedPet(itemId: String) {
        val currentPet = petState.value ?: return
        viewModelScope.launch {
            interactionSystem.feed(currentPet, itemId)
        }
    }

    fun petThePet() {
        val currentPet = petState.value ?: return
        viewModelScope.launch {
            interactionSystem.pet(currentPet)
        }
    }

    fun playWithPet() {
        val currentPet = petState.value ?: return
        viewModelScope.launch {
            interactionSystem.play(currentPet)
        }
    }

    fun toggleSleep() {
        val currentPet = petState.value ?: return
        viewModelScope.launch {
            interactionSystem.toggleSleep(currentPet)
        }
    }

    fun updatePetStat(statName: String, value: Float) {
        viewModelScope.launch {
            interactionSystem.simulationManager.updatePetState { pet ->
                val newStats = when (statName.lowercase()) {
                    "hunger" -> pet.stats.copy(hunger = value)
                    "energy" -> pet.stats.copy(energy = value)
                    "happiness" -> pet.stats.copy(happiness = value)
                    "health" -> pet.stats.copy(health = value)
                    "hygiene" -> pet.stats.copy(hygiene = value)
                    "social" -> pet.stats.copy(social = value)
                    "stress" -> pet.stats.copy(stress = value)
                    else -> pet.stats
                }
                pet.copy(stats = newStats)
            }
        }
    }

    fun tickManual(hours: Int) {
        viewModelScope.launch {
            // Jump forward in time by processing multiple ticks or one large tick
            val jumpMillis = hours * 3600000L
            val targetTime = System.currentTimeMillis() + jumpMillis
            
            // We can't easily "fake" the repository's last update timestamp from here
            // unless we modify the PetModel directly.
            interactionSystem.simulationManager.updatePetState { pet ->
                pet.copy(lastUpdateTimestamp = pet.lastUpdateTimestamp - jumpMillis)
            }
            // Then trigger a normal tick which will see the large delta
            processSimulationTickUseCase(System.currentTimeMillis())
        }
    }

    fun executeCommand(command: String) {
        val parts = command.trim().lowercase().split(" ")
        if (parts.isEmpty()) return

        when (parts[0]) {
            "/set" -> {
                if (parts.size >= 3) {
                    val stat = parts[1]
                    val value = parts[2].toFloatOrNull() ?: return
                    updatePetStat(stat, value)
                }
            }
            "/tick" -> {
                if (parts.size >= 2) {
                    val hours = parts[1].toIntOrNull() ?: return
                    tickManual(hours)
                }
            }
            "/coins" -> {
                if (parts.size >= 2) {
                    val amount = parts[1].toIntOrNull() ?: return
                    viewModelScope.launch {
                        economyRepository.addCoins(amount)
                    }
                }
            }
        }
    }
}
