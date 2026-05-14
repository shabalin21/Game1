package com.example.myapplication.domain.market

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

import timber.log.Timber

@Singleton
class MarketSimulationEngine @Inject constructor() {
    
    private val _assets = MutableStateFlow<List<AssetPrice>>(initialAssets())
    val assets: StateFlow<List<AssetPrice>> = _assets.asStateFlow()

    private var currentMarketTrend = MarketTrend.SIDEWAYS
    private var trendDurationTicks = 0
    private var marketVolatility = 1.0f

    fun tick() {
        updateMarketTrend()
        
        val current = _assets.value
        val updated = current.map { asset ->
            val trendBias = when (currentMarketTrend) {
                MarketTrend.BULL -> 0.02f
                MarketTrend.BEAR -> -0.02f
                MarketTrend.PUMP -> 0.15f
                MarketTrend.CRASH -> -0.25f
                MarketTrend.SIDEWAYS -> 0.0f
            }

            val randomChange = (Random.nextFloat() - 0.5f) * 2.0f * asset.volatility * marketVolatility
            val changePercent = trendBias + randomChange
            
            val newPrice = (asset.currentPrice * (1 + changePercent)).coerceAtLeast(0.01f)
            asset.copy(
                currentPrice = newPrice,
                history = (asset.history + newPrice).takeLast(50)
            )
        }
        _assets.value = updated
    }

    private fun updateMarketTrend() {
        if (trendDurationTicks > 0) {
            trendDurationTicks--
            // Occasionally spike volatility
            if (Random.nextFloat() < 0.05f) {
                marketVolatility = 1.5f + Random.nextFloat() * 2.0f
            } else {
                marketVolatility = 1.0f
            }
            return
        }

        // Change Trend
        val nextTrend = Random.nextInt(100)
        currentMarketTrend = when {
            nextTrend < 40 -> MarketTrend.SIDEWAYS
            nextTrend < 70 -> MarketTrend.BULL
            nextTrend < 90 -> MarketTrend.BEAR
            nextTrend < 95 -> MarketTrend.PUMP
            else -> MarketTrend.CRASH
        }
        
        trendDurationTicks = when (currentMarketTrend) {
            MarketTrend.PUMP -> Random.nextInt(3, 8)
            MarketTrend.CRASH -> Random.nextInt(2, 5)
            else -> Random.nextInt(10, 30)
        }
        
        Timber.i("Market: Trend changed to $currentMarketTrend for $trendDurationTicks ticks")
    }

    private fun initialAssets() = listOf(
        AssetPrice("BTC", "Bitcoin", 65000f, 0.03f, AssetType.CRYPTO),
        AssetPrice("ETH", "Ethereum", 3500f, 0.04f, AssetType.CRYPTO),
        // ... rest of assets
        AssetPrice("SOL", "Solana", 150f, 0.12f, AssetType.CRYPTO),
        
        AssetPrice("MCORP", "MegaCorp", 1200f, 0.02f, AssetType.STOCK),
        AssetPrice("NTECH", "Neural Systems", 800f, 0.04f, AssetType.STOCK),
        AssetPrice("BGEN", "BioLogics", 450f, 0.06f, AssetType.STOCK),
        
        AssetPrice("GBOND", "Gov Bonds", 100f, 0.005f, AssetType.SECURITIES),
        AssetPrice("ETF", "S&P 500 Index", 500f, 0.01f, AssetType.SECURITIES),
        
        AssetPrice("GOLD", "Gold Bullion", 2300f, 0.002f, AssetType.GOLD),
        AssetPrice("SILVER", "Silver Bars", 28f, 0.008f, AssetType.SILVER)
    )
}

data class AssetPrice(
    val id: String,
    val name: String,
    val currentPrice: Float,
    val volatility: Float,
    val type: AssetType,
    val history: List<Float> = listOf(currentPrice)
)

enum class AssetType {
    CRYPTO, STOCK, SECURITIES, GOLD, SILVER, CASH
}

enum class MarketTrend {
    BULL, BEAR, SIDEWAYS, PUMP, CRASH
}
