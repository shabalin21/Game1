package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.ItemModel
import com.example.myapplication.domain.model.ItemRarity
import kotlinx.coroutines.flow.Flow

data class InventoryItem(
    val id: Long,
    val itemId: String,
    val quantity: Int,
    val rarity: ItemRarity,
    val floatValue: Float? = null
)

interface EconomyRepository {
    fun getCoins(): Flow<Int>
    suspend fun addCoins(amount: Int)
    suspend fun spendCoins(amount: Int): Boolean
    
    fun getInventory(): Flow<List<InventoryItem>>
    suspend fun addItem(itemId: String, quantity: Int = 1, rarity: ItemRarity = ItemRarity.COMMON, floatValue: Float? = null)
    suspend fun removeItem(id: Long, quantity: Int = 1): Boolean

    // Transactional Store Operations
    suspend fun purchaseConsumable(itemId: String, price: Int, quantity: Int = 1, rarity: ItemRarity, floatValue: Float? = null): Result<Unit>
    suspend fun purchasePermanent(itemId: String, price: Int, transform: suspend (com.example.myapplication.domain.model.PetModel) -> com.example.myapplication.domain.model.PetModel): Result<Unit>
}
