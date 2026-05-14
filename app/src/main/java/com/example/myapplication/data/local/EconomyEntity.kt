package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_economy")
data class EconomyEntity(
    @PrimaryKey val userId: String = "default_user",
    val coins: Int = 500
)
