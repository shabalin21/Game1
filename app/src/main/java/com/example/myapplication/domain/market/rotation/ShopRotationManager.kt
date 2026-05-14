package com.example.myapplication.domain.market.rotation

import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.WorldRepository
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ShopRotationManager @Inject constructor(
    private val worldRepository: WorldRepository
) {
    private val _luxurySelection = MutableStateFlow<List<String>>(emptyList())
    val luxurySelection: StateFlow<List<String>> = _luxurySelection.asStateFlow()

    private val _blackMarketSelection = MutableStateFlow<List<String>>(emptyList())
    val blackMarketSelection: StateFlow<List<String>> = _blackMarketSelection.asStateFlow()

    private var lastRotationDay = -1

    suspend fun tick(currentTimeMillis: Long) {
        val worldState = worldRepository.getWorldState().first()
        val currentDay = (currentTimeMillis / (24 * 60 * 60 * 1000L)).toInt()

        if (currentDay != lastRotationDay) {
            rotateShops()
            lastRotationDay = currentDay
        }
    }

    private fun rotateShops() {
        Timber.i("Shops: Rotating selections")
        
        // Luxury Shop: 5 random high-end items
        val allLuxury = ItemRegistry.allItems.filter { it.price >= 50000 || it.rarity >= ItemRarity.EPIC }
        _luxurySelection.value = allLuxury.shuffled().take(5).map { it.id }

        // Black Market: 2-3 rare/mythic items, 10% chance to be open
        if (Random.nextFloat() < 0.1f) {
            val allRare = ItemRegistry.allItems.filter { it.rarity >= ItemRarity.RARE }
            _blackMarketSelection.value = allRare.shuffled().take(3).map { it.id }
            Timber.w("Shops: Black Market is OPEN")
        } else {
            _blackMarketSelection.value = emptyList()
        }
    }
}
