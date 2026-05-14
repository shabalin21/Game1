package com.example.myapplication.ui.screen.progression

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.progression.PrestigeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrestigeViewModel @Inject constructor(
    val prestigeManager: PrestigeManager
) : ViewModel()
