package com.example.myapplication.ui.screen.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.LifetimeStats
import com.example.myapplication.domain.market.*
import com.example.myapplication.domain.repository.EconomyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val marketEngine: MarketSimulationEngine,
    private val portfolioManager: PortfolioManager,
    private val economyRepository: EconomyRepository,
    private val statisticsRepository: com.example.myapplication.domain.repository.StatisticsRepository
) : ViewModel() {

    val assets = marketEngine.assets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolio = portfolioManager.ownedAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    
    val costBasis = portfolioManager.costBasis
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val statistics = statisticsRepository.getStatistics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LifetimeStats())

    val coins = economyRepository.getCoins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val portfolioValue = portfolioManager.getPortfolioValue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    fun buyAsset(assetId: String, amount: Float) {
        viewModelScope.launch {
            portfolioManager.buyAsset(assetId, amount)
        }
    }

    fun sellAsset(assetId: String, amount: Float) {
        viewModelScope.launch {
            portfolioManager.sellAsset(assetId, amount)
        }
    }
}
