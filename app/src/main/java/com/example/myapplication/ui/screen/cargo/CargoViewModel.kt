package com.example.myapplication.ui.screen.cargo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.InventoryItem
import com.example.myapplication.domain.repository.InventoryRepository
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.simulation.SimulationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CargoViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val economyRepository: EconomyRepository,
    private val simulationManager: SimulationManager,
    private val petRepository: PetRepository
) : ViewModel() {

    val petState: StateFlow<PetModel?> = petRepository.getPetState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val coins: StateFlow<Int> = economyRepository.getCoins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val inventory: StateFlow<List<InventoryItem>> = inventoryRepository.getInventory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketItems: List<ItemModel> = ItemRegistry.allItems
    
    val recommendedItems: List<ItemModel> = ItemRegistry.allItems.filter { it.rarity >= ItemRarity.RARE }.shuffled().take(3)

    fun purchaseItem(itemId: String) {
        viewModelScope.launch {
            val item = ItemRegistry.getItem(itemId) ?: return@launch
            val pet = petState.value ?: return@launch
            if (!item.isConsumable && pet.ownedPermanentIds.contains(item.id)) {
                return@launch
            }

            if (economyRepository.spendCoins(item.price)) {
                simulationManager.purchaseItem(item)
            }
        }
    }

    fun processItem(itemId: String) {
        viewModelScope.launch {
            simulationManager.useItem(itemId)
        }
    }
}
