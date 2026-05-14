package com.example.myapplication.ui.screen.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.repository.InventoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import timber.log.Timber
import kotlin.random.Random

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val economyRepository: EconomyRepository,
    private val petRepository: PetRepository,
    private val shopRotationManager: com.example.myapplication.domain.market.rotation.ShopRotationManager
) : ViewModel() {

    val luxurySelection = shopRotationManager.luxurySelection
    val blackMarketSelection = shopRotationManager.blackMarketSelection

    private val _selectedCategory = MutableStateFlow(ItemCategory.PRODUCTS)
    val selectedCategory: StateFlow<ItemCategory> = _selectedCategory.asStateFlow()

    private val _purchaseResult = MutableSharedFlow<Result<String>>()
    val purchaseResult = _purchaseResult.asSharedFlow()

    val coins: StateFlow<Int> = economyRepository.getCoins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    val petState = petRepository.getPetState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val inventory: StateFlow<List<InventoryItem>> = economyRepository.getInventory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allItems = ItemRegistry.allItems

    fun selectCategory(category: ItemCategory) {
        _selectedCategory.value = category
    }

    fun claimMission(missionId: String) {
        // Implementation for claiming rewards
    }

    fun buyItem(item: ItemModel) {
        viewModelScope.launch {
            val generatedFloat = if (item.rarity == ItemRarity.GOLD) {
                0.9f + (Random.nextFloat() * 0.1f)
            } else null
            
            val result = if (item.isConsumable) {
                economyRepository.purchaseConsumable(
                    itemId = item.id,
                    price = item.price,
                    quantity = 1,
                    rarity = item.rarity,
                    floatValue = item.floatValue ?: generatedFloat
                )
            } else {
                economyRepository.purchasePermanent(item.id, item.price) { pet ->
                    val newOwned = pet.ownedPermanentIds.toMutableSet()
                    newOwned.add(item.id)
                    
                    // Automatically equip homes
                    if (item.category == ItemCategory.HOMES) {
                        val newEquipped = pet.equippedItems.toMutableMap()
                        newEquipped[ItemCategory.HOMES] = item.id
                        pet.copy(ownedPermanentIds = newOwned, equippedItems = newEquipped)
                    } else {
                        pet.copy(ownedPermanentIds = newOwned)
                    }
                }
            }
            
            if (result.isSuccess) {
                Timber.i("Shop: Successfully purchased ${item.id}")
                _purchaseResult.emit(Result.success("Purchased ${item.name}!"))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Timber.e("Shop: Purchase failed for ${item.id}: $error")
                _purchaseResult.emit(Result.failure(Exception("Purchase failed: $error")))
            }
        }
    }
}
