package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pet_state LIMIT 1")
    fun getPetState(): Flow<PetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)

    @Update
    suspend fun updatePet(pet: PetEntity)

    @Query("DELETE FROM pet_state")
    suspend fun deleteAll()

    // Economy & Inventory
    @Query("SELECT * FROM user_economy WHERE userId = :userId")
    fun getEconomy(userId: String = "default_user"): Flow<EconomyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEconomy(economy: EconomyEntity)

    @Query("SELECT * FROM inventory")
    fun getInventory(): Flow<List<InventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInventoryItem(item: InventoryEntity)

    @Query("SELECT * FROM inventory WHERE itemId = :itemId AND rarity = :rarity AND (floatValue = :floatValue OR (floatValue IS NULL AND :floatValue IS NULL)) LIMIT 1")
    suspend fun getStackableItem(itemId: String, rarity: String, floatValue: Float?): InventoryEntity?

    @Query("SELECT * FROM inventory WHERE id = :id")
    suspend fun getInventoryItemById(id: Long): InventoryEntity?

    @Query("DELETE FROM inventory WHERE id = :id")
    suspend fun removeInventoryItemById(id: Long)

    @Query("DELETE FROM inventory")
    suspend fun deleteAllInventory()

    @androidx.room.Transaction
    suspend fun purchaseConsumable(userId: String, price: Int, itemId: String, quantity: Int, rarity: String, floatValue: Float? = null) {
        val economy = getEconomySync(userId) ?: throw IllegalStateException("Economy not found")
        if (economy.coins < price) throw IllegalStateException("Insufficient funds")
        
        updateEconomy(economy.copy(coins = economy.coins - price))
        
        // Find if a stackable item exists
        val existing = getStackableItem(itemId, rarity, floatValue)
        if (existing != null) {
            updateInventoryItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            updateInventoryItem(InventoryEntity(itemId = itemId, quantity = quantity, rarity = rarity, floatValue = floatValue))
        }
    }

    @androidx.room.Transaction
    suspend fun purchasePermanent(userId: String, price: Int, petEntity: PetEntity) {
        val economy = getEconomySync(userId) ?: throw IllegalStateException("Economy not found")
        if (economy.coins < price) throw IllegalStateException("Insufficient funds")

        updateEconomy(economy.copy(coins = economy.coins - price))
        updatePet(petEntity)
    }

    @Query("SELECT * FROM user_economy WHERE userId = :userId")
    suspend fun getEconomySync(userId: String): EconomyEntity?

    // Settings
    @Query("SELECT * FROM app_settings WHERE userId = :userId")
    fun getSettings(userId: String = "default_user"): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: SettingsEntity)

    @Query("DELETE FROM user_economy")
    suspend fun resetEconomy()

    // Upgrades
    @Query("SELECT * FROM permanent_upgrades")
    fun getAllUpgrades(): Flow<List<UpgradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUpgrade(upgrade: UpgradeEntity)

    @Query("SELECT * FROM permanent_upgrades WHERE id = :id")
    suspend fun getUpgradeById(id: String): UpgradeEntity?

    @Query("DELETE FROM permanent_upgrades")
    suspend fun deleteAllUpgrades()

    @androidx.room.Transaction
    suspend fun fullGameReset() {
        deleteAll()
        deleteAllInventory()
        deleteAllUpgrades()
        resetEconomy()
        updateDebugCheats(DebugCheatEntity()) // Reset cheats too
    }

    // Debug / Cheats
    @Query("SELECT * FROM debug_cheats WHERE id = 'global_cheats'")
    fun getDebugCheats(): Flow<DebugCheatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDebugCheats(cheats: DebugCheatEntity)
}
