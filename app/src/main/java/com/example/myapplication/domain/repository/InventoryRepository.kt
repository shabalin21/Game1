package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.ItemRarity
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getInventory(): Flow<List<InventoryItem>>
    suspend fun addItem(itemId: String, quantity: Int = 1, rarity: ItemRarity = ItemRarity.COMMON, floatValue: Float? = null)
    suspend fun removeItem(id: Long, quantity: Int = 1): Boolean
}
