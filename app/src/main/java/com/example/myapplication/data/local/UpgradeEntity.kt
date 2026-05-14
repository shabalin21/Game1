package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permanent_upgrades")
data class UpgradeEntity(
    @PrimaryKey val id: String,
    val level: Int
)
