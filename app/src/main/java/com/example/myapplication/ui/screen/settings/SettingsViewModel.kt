package com.example.myapplication.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.CanonicalState
import com.example.myapplication.data.local.PetDao
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.SettingsRepository
import com.example.myapplication.domain.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val statisticsRepository: StatisticsRepository,
    private val canonicalState: CanonicalState,
    private val petDao: PetDao
) : ViewModel() {

    val settings: StateFlow<SettingsModel> = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsModel())

    val statistics = statisticsRepository.getStatistics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LifetimeStats())

    val petState = canonicalState.state.map { it.pet }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateGraphics(graphics: GraphicsSettings) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(graphics = graphics))
        }
    }

    fun updateAudio(audio: AudioSettings) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(audio = audio))
        }
    }

    fun updateGameplay(gameplay: GameplaySettings) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(gameplay = gameplay))
        }
    }

    fun updateUi(ui: UiSettings) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(ui = ui))
        }
    }

    fun resetSave() {
        viewModelScope.launch {
            petDao.fullGameReset()
            settingsRepository.resetSettings()
        }
    }
}
