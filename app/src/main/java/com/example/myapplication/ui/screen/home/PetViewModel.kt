package com.example.myapplication.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.CanonicalState
import com.example.myapplication.domain.admin.CheatManager
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.InventoryRepository
import com.example.myapplication.domain.social.SocialManager
import com.example.myapplication.domain.market.MarketSimulationEngine
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.usecase.ProcessSimulationTickUseCase
import com.example.myapplication.ui.render.RenderState
import com.example.myapplication.ui.render.SnapshotReducer
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
    private val intentDispatcher: com.example.myapplication.domain.event.IntentDispatcher,
    private val inventoryRepository: InventoryRepository,
    private val canonicalState: CanonicalState,
    private val processSimulationTickUseCase: ProcessSimulationTickUseCase,
    private val marketEngine: MarketSimulationEngine,
    private val socialManager: SocialManager,
    private val snapshotReducer: SnapshotReducer,
    val cheatManager: CheatManager,
    private val petRepository: PetRepository // Added for manual tick logic
) : ViewModel() {

    val gameState = canonicalState.state

    val renderState = gameState.map { snapshotReducer.reduce(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Pet.Feed(itemId))
    }

    fun petThePet() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Pet.Petting)
    }

    fun playWithPet() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Pet.Play)
    }

    fun toggleSleep() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Pet.ToggleSleep)
    }

    fun tickManual(hours: Int) {
        viewModelScope.launch {
            // Jump forward in time by processing multiple ticks or one large tick
            val jumpMillis = hours * 3600000L
            
            val currentPet = petRepository.getPetState().firstOrNull() ?: return@launch
            petRepository.savePetState(currentPet.copy(lastUpdateTimestamp = currentPet.lastUpdateTimestamp - jumpMillis))
            
            // Then trigger a normal tick which will see the large delta
            processSimulationTickUseCase(System.currentTimeMillis())
        }
    }

    fun executeCommand(command: String) {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Admin.ExecuteCommand(command))
    }
}
