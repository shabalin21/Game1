package com.example.myapplication.data.repository

import com.example.myapplication.data.local.InventoryEntity
import com.example.myapplication.data.local.PetDao
import com.example.myapplication.domain.model.ItemRarity
import com.example.myapplication.domain.repository.InventoryItem
import com.example.myapplication.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val petDao: PetDao
) : InventoryRepository {

    override fun getInventory(): Flow<List<InventoryItem>> {
        return petDao.getInventory().map { list ->
            list.map { entity ->
                InventoryItem(
                    id = entity.id,
                    itemId = entity.itemId,
                    quantity = entity.quantity,
                    rarity = try { ItemRarity.valueOf(entity.rarity) } catch (e: Exception) { ItemRarity.COMMON },
                    floatValue = entity.floatValue
                )
            }
        }
    }

    override suspend fun addItem(itemId: String, quantity: Int, rarity: ItemRarity, floatValue: Float?) {
        val existing = petDao.getStackableItem(itemId, rarity.name, floatValue)
        if (existing != null) {
            petDao.updateInventoryItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            petDao.updateInventoryItem(InventoryEntity(itemId = itemId, quantity = quantity, rarity = rarity.name, floatValue = floatValue))
        }
    }

    override suspend fun removeItem(id: Long, quantity: Int): Boolean {
        val existing = petDao.getInventoryItemById(id)
        return if (existing != null && existing.quantity >= quantity) {
            if (existing.quantity == quantity) {
                petDao.removeInventoryItemById(id)
            } else {
                petDao.updateInventoryItem(existing.copy(quantity = existing.quantity - quantity))
            }
            true
        } else {
            false
        }
    }
}
