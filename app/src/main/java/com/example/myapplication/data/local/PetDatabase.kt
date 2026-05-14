package com.example.myapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PetEntity::class, 
        InventoryEntity::class, 
        EconomyEntity::class, 
        SettingsEntity::class, 
        UpgradeEntity::class,
        StatisticsEntity::class,
        DebugCheatEntity::class
    ], 
    version = 15,
    exportSchema = false
)
abstract class PetDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun statisticsDao(): StatisticsDao
}
