package com.example.myapplication.ui.animation

import androidx.lifecycle.ViewModel
import com.example.myapplication.ui.debug.DevLabManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.myapplication.domain.repository.SettingsRepository
import com.example.myapplication.domain.repository.WorldRepository
import com.example.myapplication.domain.model.Weather
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class JuiceViewModel @Inject constructor(
    val juiceManager: JuiceManager,
    val devLabManager: DevLabManager,
    private val settingsRepository: SettingsRepository,
    private val worldRepository: WorldRepository
) : ViewModel() {

    val targetFps = settingsRepository.getSettings()
        .map { it.graphics.targetFps }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60)

    val weather = worldRepository.getWorldState()
        .map { it.weather }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Weather.SUNNY)
}
