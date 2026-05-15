package com.example.myapplication

import android.app.Application
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.repository.StatisticsRepository
import com.example.myapplication.domain.stats.StatisticsManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class PetApplication : Application() {

    @Inject
    lateinit var worldKernel: com.example.myapplication.core.WorldKernel

    @Inject
    lateinit var statisticsManager: StatisticsManager

    @Inject
    lateinit var petRepository: PetRepository

    @Inject
    lateinit var statisticsRepository: StatisticsRepository

    @Inject
    lateinit var juiceBridge: com.example.myapplication.ui.animation.JuiceBridge

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
                
                Timber.d("Application: Booting WorldKernel...")
                statisticsManager.start()
                juiceBridge.start()
                worldKernel.boot()
                Timber.i("Application: Initialization complete.")
            } catch (e: Exception) {
                Timber.e(e, "CRITICAL: Application initialization failed")
            }
        }
    }
}
