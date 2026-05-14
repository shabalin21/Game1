package com.example.myapplication.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.domain.usecase.ProcessSimulationTickUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Background worker to keep the simulation alive even when the app is closed.
 */
@HiltWorker
class SimulationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val processSimulationTickUseCase: ProcessSimulationTickUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.i("SimulationWorker started.")
        
        return try {
            processSimulationTickUseCase(System.currentTimeMillis())
            Timber.i("SimulationWorker: Simulation tick processed.")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SimulationWorker failed")
            Result.retry()
        }
    }
}
