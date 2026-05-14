package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: String,
    val quantity: Int,
    val rarity: String,
    val floatValue: Float? = null
)

@Entity(tableName = "user_economy")
data class EconomyEntity(
    @PrimaryKey val userId: String = "default_user",
    val coins: Int = 500
)
