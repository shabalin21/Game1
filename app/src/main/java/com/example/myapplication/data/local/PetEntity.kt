package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the pet state.
 */
@Entity(tableName = "pet_state")
data class PetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val statsJson: String,
    val birthTimestamp: Long,
    val lastUpdateTimestamp: Long,
    val level: Int,
    val xp: Long,
    val isSleeping: Boolean,
    val psychologyJson: String,
    val emotionStateJson: String,
    val evolutionStage: String,
    val lifetimeStatsJson: String,
    val employmentJson: String = "{}",
    val equippedItemsJson: String = "{}",
    val savedOutfitsJson: String = "{}",
    val ownedPermanentIdsJson: String = "[]",
    val activeModifiersJson: String = "[]",
    val appearanceJson: String = "{}",
    val socialJson: String = "{}",
    val casinoSessionJson: String = "{}",
    val ownedAssetsJson: String = "{}",
    val assetCostBasisJson: String = "{}",
    val missionsJson: String = "{}",
    val collectionLogJson: String = "[]"
)
