package com.example.myapplication

import android.app.Application
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.repository.StatisticsRepository
import com.example.myapplication.domain.simulation.GameLoopManager
import com.example.myapplication.domain.stats.StatisticsManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class PetApplication : Application() {

    @Inject
    lateinit var gameLoopManager: GameLoopManager

    @Inject
    lateinit var statisticsManager: StatisticsManager

    @Inject
    lateinit var petRepository: PetRepository

    @Inject
    lateinit var statisticsRepository: StatisticsRepository

    @Inject
    lateinit var missionDispatcher: com.example.myapplication.domain.progression.mission.MissionDispatcher

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.i("Initializing Pet Application...")

        applicationScope.launch {
            try {
                Timber.d("Application: Ensuring initial data exists...")
                withContext(Dispatchers.IO) {
                    petRepository.ensurePetExists()
                    statisticsRepository.ensureStatisticsExists()
                }
                
                Timber.d("Application: Starting managers...")
                statisticsManager.start()
                missionDispatcher.start()
                gameLoopManager.start()
                Timber.i("Application: Initialization complete.")
            } catch (e: Exception) {
                Timber.e(e, "CRITICAL: Application initialization failed")
            }
        }
    }
}
