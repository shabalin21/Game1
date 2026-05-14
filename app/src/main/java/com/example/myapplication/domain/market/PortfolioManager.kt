package com.example.myapplication.domain.market

import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioManager @Inject constructor(
    private val economyRepository: EconomyRepository,
    private val petRepository: PetRepository,
    private val marketEngine: MarketSimulationEngine,
    private val logManager: TerminalLogManager,
    private val statisticsRepository: com.example.myapplication.domain.repository.StatisticsRepository
) {
    val ownedAssets: StateFlow<Map<String, Float>> = petRepository.getPetState()
        .map { it?.ownedAssets ?: emptyMap() }
        .stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, emptyMap())
    
    val costBasis: StateFlow<Map<String, Float>> = petRepository.getPetState()
        .map { it?.assetCostBasis ?: emptyMap() }
        .stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, emptyMap())

    suspend fun buyAsset(assetId: String, amount: Float): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        val asset = marketEngine.assets.value.find { it.id == assetId } ?: return false
        val cost = (asset.currentPrice * amount).toInt()
        
        if (economyRepository.spendCoins(cost)) {
            val newOwnedAssets = pet.ownedAssets.toMutableMap()
            newOwnedAssets[assetId] = newOwnedAssets.getOrDefault(assetId, 0f) + amount
            
            val newCostBasis = pet.assetCostBasis.toMutableMap()
            newCostBasis[assetId] = newCostBasis.getOrDefault(assetId, 0f) + cost

            statisticsRepository.updateMarketStats(invested = cost, earned = 0)
            logManager.log(LogCategory.ECONOMY, "Purchased $amount units of ${asset.name}.")
            
            petRepository.savePetState(pet.copy(
                ownedAssets = newOwnedAssets,
                assetCostBasis = newCostBasis,
                psychology = pet.psychology.copy(
                    dopamineLevel = (pet.psychology.dopamineLevel + 5f).coerceAtMost(100f)
                )
            ))
            return true
        }
        return false
    }

    suspend fun sellAsset(assetId: String, amount: Float): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        val asset = marketEngine.assets.value.find { it.id == assetId } ?: return false
        val owned = pet.ownedAssets.getOrDefault(assetId, 0f)
        
        if (owned >= amount && owned > 0) {
            val gain = (asset.currentPrice * amount).toInt()
            
            // Calculate Profit
            val basisPerUnit = pet.assetCostBasis.getOrDefault(assetId, 0f) / owned
            val costOfSoldUnits = (basisPerUnit * amount).toInt()
            val profit = gain - costOfSoldUnits

            economyRepository.addCoins(gain)
            
            val newOwnedAssets = pet.ownedAssets.toMutableMap()
            newOwnedAssets[assetId] = owned - amount
            
            val newCostBasis = pet.assetCostBasis.toMutableMap()
            newCostBasis[assetId] = (newCostBasis.getOrDefault(assetId, 0f) - costOfSoldUnits).coerceAtLeast(0f)

            statisticsRepository.logMarketTrade(profit)
            statisticsRepository.updateMarketStats(invested = 0, earned = gain)

            logManager.log(LogCategory.ECONOMY, "Sold $amount units of ${asset.name} for $gain coins. Profit: $profit")
            
            petRepository.savePetState(pet.copy(
                ownedAssets = newOwnedAssets,
                assetCostBasis = newCostBasis,
                stats = pet.stats.copy(stress = (pet.stats.stress + 2f).coerceAtMost(100f))
            ))
            return true
        }
        return false
    }

    fun getPortfolioValue(): Flow<Float> {
        return combine(marketEngine.assets, petRepository.getPetState()) { market, pet ->
            val owned = pet?.ownedAssets ?: emptyMap()
            owned.map { (id, qty) ->
                val price = market.find { it.id == id }?.currentPrice ?: 0f
                price * qty
            }.sum()
        }
    }
}
