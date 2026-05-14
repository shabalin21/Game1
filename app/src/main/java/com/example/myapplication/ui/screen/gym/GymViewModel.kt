package com.example.myapplication.ui.screen.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.gym.engine.GymManager
import com.example.myapplication.domain.gym.model.Exercise
import com.example.myapplication.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GymViewModel @Inject constructor(
    private val gymManager: GymManager,
    private val petRepository: PetRepository
) : ViewModel() {

    val petState = petRepository.getPetState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun startTraining(exercise: Exercise) {
        viewModelScope.launch {
            gymManager.train(exercise)
        }
    }
}
