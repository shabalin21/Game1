package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.local.PetDao
import com.example.myapplication.data.local.PetDatabase
import com.example.myapplication.data.local.StatisticsDao
import com.example.myapplication.data.repository.*
import com.example.myapplication.domain.event.GameplayEventManager
import com.example.myapplication.domain.repository.*
import com.example.myapplication.domain.simulation.SimulationEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePetDatabase(@ApplicationContext context: Context): PetDatabase {
        return Room.databaseBuilder(
            context,
            PetDatabase::class.java,
            "pet_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePetDao(database: PetDatabase): PetDao {
        return database.petDao()
    }

    @Provides
    fun provideStatisticsDao(database: PetDatabase): StatisticsDao {
        return database.statisticsDao()
    }

    @Provides
    @Singleton
    fun provideStatisticsRepository(
        statisticsDao: StatisticsDao
    ): StatisticsRepository {
        return StatisticsRepositoryImpl(statisticsDao)
    }

    @Provides
    @Singleton
    fun providePetRepository(
        petDao: PetDao,
        json: Json
    ): PetRepository {
        return PetRepositoryImpl(
            petDao = petDao,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideEconomyRepository(
        petDao: PetDao,
        eventManager: GameplayEventManager,
        cheatManager: com.example.myapplication.domain.admin.CheatManager,
        json: Json
    ): EconomyRepository {
        return EconomyRepositoryImpl(
            petDao = petDao,
            eventManager = eventManager,
            cheatManager = cheatManager,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(
        petDao: PetDao
    ): InventoryRepository {
        return InventoryRepositoryImpl(petDao)
    }

    @Provides
    @Singleton
    fun provideWorldRepository(
        @ApplicationContext context: Context,
        json: Json
    ): WorldRepository {
        return WorldRepositoryImpl(context, json)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        petDao: PetDao,
        json: Json
    ): SettingsRepository {
        return SettingsRepositoryImpl(petDao, json)
    }

    @Provides
    @Singleton
    fun provideUpgradeRepository(
        petDao: PetDao,
        economyRepository: EconomyRepository
    ): UpgradeRepository {
        return UpgradeRepositoryImpl(petDao, economyRepository)
    }

    @Provides
    @Singleton
    fun providePrestigeManager(
        petRepository: PetRepository,
        economyRepository: EconomyRepository,
        statisticsRepository: StatisticsRepository
    ): com.example.myapplication.domain.progression.PrestigeManager {
        return com.example.myapplication.domain.progression.PrestigeManager(
            petRepository,
            economyRepository,
            statisticsRepository
        )
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }
}
