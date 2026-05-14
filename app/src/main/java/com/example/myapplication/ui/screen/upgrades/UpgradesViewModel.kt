package com.example.myapplication.ui.screen.upgrades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.UpgradeModel
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.UpgradeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpgradesViewModel @Inject constructor(
    private val upgradeRepository: UpgradeRepository,
    private val economyRepository: EconomyRepository
) : ViewModel() {

    val upgrades: StateFlow<List<UpgradeModel>> = upgradeRepository.getUpgrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coins: StateFlow<Int> = economyRepository.getCoins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun buyUpgrade(upgradeId: String) {
        viewModelScope.launch {
            upgradeRepository.buyUpgrade(upgradeId)
        }
    }
}
