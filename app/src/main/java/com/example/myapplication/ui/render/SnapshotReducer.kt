package com.example.myapplication.ui.render

import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.simulation.atmosphere.AtmosphereDirector
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SNAPSHOT REDUCER.
 * Pure mapping logic to transform GameState into RenderState.
 */
@Singleton
class SnapshotReducer @Inject constructor(
    private val atmosphereDirector: AtmosphereDirector
) {

    fun reduce(gameState: GameState): RenderState {
        val pet = gameState.pet ?: return createDefaultRenderState()
        
        val atmosphere = atmosphereDirector.resolve(pet, gameState.world)
        
        return RenderState(
            petName = pet.name,
            stats = DisplayStats(
                hunger = pet.stats.hunger,
                thirst = pet.stats.thirst,
                energy = pet.stats.energy,
                happiness = pet.stats.happiness,
                stress = pet.psychology.stress,
                health = pet.stats.health,
                hygiene = pet.stats.hygiene,
                mentalEnergy = pet.stats.mentalEnergy,
                motivation = pet.psychology.motivation,
                burnout = pet.psychology.burnout
            ),
            atmosphere = atmosphere,
            world = DisplayWorld(
                timeLabel = gameState.world.timeOfDay.name,
                weatherIcon = when(gameState.world.weather) {
                    Weather.SUNNY -> "☀️"
                    Weather.RAINY -> "🌧️"
                    Weather.CLOUDY -> "☁️"
                    Weather.STORMY -> "⛈️"
                },
                temperatureLabel = "${gameState.world.temperature}°C"
            ),
            activeBuffs = pet.activeModifiers.map { mod ->
                val totalDuration = 1000 * 60 * 5 // TODO: Store total duration in modifier
                val remaining = (mod.expirationTimestamp - System.currentTimeMillis()).toFloat()
                DisplayBuff(
                    id = mod.id,
                    label = mod.name,
                    icon = mod.icon,
                    progress = (remaining / totalDuration).coerceIn(0f, 1f)
                )
            },
            activityName = pet.psychology.currentActivity.name,
            coins = gameState.coins,
            btcPrice = 0f, // TODO: Get from economy state
            
            primaryMood = pet.emotionState.primaryMood,
            moodIntensity = pet.emotionState.intensity,
            isSleeping = pet.isSleeping,
            appearance = pet.appearance,
            equippedItems = pet.equippedItems
        )
    }

    private fun createDefaultRenderState() = RenderState(
        petName = "...",
        stats = DisplayStats(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        atmosphere = com.example.myapplication.domain.simulation.atmosphere.AtmosphereState(),
        world = DisplayWorld("...", "...", "..."),
        activeBuffs = emptyList(),
        activityName = "...",
        coins = 0,
        btcPrice = 0f,
        primaryMood = Mood.RELAXED,
        moodIntensity = 0.5f,
        isSleeping = false,
        appearance = BuddyAppearance(),
        equippedItems = emptyMap()
    )
}
