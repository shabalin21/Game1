package com.example.myapplication.ui.screen.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.work.engine.JobManager
import com.example.myapplication.domain.work.model.Job
import com.example.myapplication.domain.work.model.JobRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val jobManager: JobManager,
    private val petRepository: PetRepository
) : ViewModel() {

    val petState = petRepository.getPetState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val careerState = jobManager.careerState
    val marketTrends = jobManager.marketTrends
    val availableJobs = JobRegistry.allJobs

    fun applyForJob(job: Job) {
        viewModelScope.launch {
            jobManager.applyForJob(job)
        }
    }

    fun work(jobId: String) {
        viewModelScope.launch {
            jobManager.work(jobId)
        }
    }

    fun promote() {
        viewModelScope.launch {
            jobManager.promote()
        }
    }
}
