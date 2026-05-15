package com.example.myapplication.data.repository

import com.example.myapplication.core.EventBus
import com.example.myapplication.data.local.*
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.TransactionType
import com.example.myapplication.domain.model.ItemRarity
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.InventoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EconomyRepositoryImpl @Inject constructor(
    private val petDao: PetDao,
    private val eventBus: EventBus,
    private val cheatManager: com.example.myapplication.domain.admin.CheatManager,
    private val json: Json
) : EconomyRepository {

    override fun getCoins(): Flow<Int> {
        return petDao.getEconomy().map { 
            if (cheatManager.infiniteMoney.value) 9999999 else it?.coins ?: 100
        }
    }

    override suspend fun addCoins(amount: Int) {
        val current = petDao.getEconomy().first() ?: EconomyEntity()
        petDao.updateEconomy(current.copy(coins = current.coins + amount))
        eventBus.publish(GameplayEvent.EconomyTransaction(amount, TransactionType.EARNED, "generic"))
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        val current = petDao.getEconomy().first() ?: EconomyEntity()
        return if (current.coins >= amount) {
            petDao.updateEconomy(current.copy(coins = current.coins - amount))
            eventBus.publish(GameplayEvent.EconomyTransaction(amount, TransactionType.SPENT, "generic"))
            true
        } else {
            false
        }
    }

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

    override suspend fun purchaseConsumable(itemId: String, price: Int, quantity: Int, rarity: ItemRarity, floatValue: Float?): Result<Unit> {
        return try {
            Timber.i("Store: Attempting purchase of $quantity x $itemId ($rarity) for $price CR")
            petDao.purchaseConsumable("default_user", price, itemId, quantity, rarity.name, floatValue)
            eventBus.publish(GameplayEvent.EconomyTransaction(price, TransactionType.SPENT, "store_purchase"))
            Timber.i("Store: Successfully purchased $itemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Store: Purchase failed for $itemId")
            Result.failure(e)
        }
    }

    override suspend fun purchasePermanent(
        itemId: String,
        price: Int,
        transform: suspend (com.example.myapplication.domain.model.PetModel) -> com.example.myapplication.domain.model.PetModel
    ): Result<Unit> {
        return try {
            Timber.i("Store: Attempting permanent purchase of $itemId for $price CR")
            val currentPet = petDao.getPetState().first()?.toDomain(json) ?: throw IllegalStateException("Pet not found")
            val updatedPet = transform(currentPet)
            
            petDao.purchasePermanent("default_user", price, updatedPet.toEntity(json))
            eventBus.publish(GameplayEvent.EconomyTransaction(price, TransactionType.SPENT, "store_purchase_permanent"))
            Timber.i("Store: Successfully purchased permanent item $itemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Store: Permanent purchase failed for $itemId")
            Result.failure(e)
        }
    }
}
