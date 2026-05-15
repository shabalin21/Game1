package com.example.myapplication.ui.render

import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.simulation.atmosphere.AtmosphereState
import kotlinx.serialization.Serializable

/**
 * IMMUTABLE RENDER DTO.
 * The only object the UI should ever consume.
 */
@Serializable
data class RenderState(
    val petName: String,
    val stats: DisplayStats,
    val atmosphere: AtmosphereState,
    val world: DisplayWorld,
    val activeBuffs: List<DisplayBuff>,
    val activityName: String,
    val coins: Int,
    val btcPrice: Float,
    
    // Character Visuals
    val primaryMood: Mood,
    val moodIntensity: Float,
    val isSleeping: Boolean,
    val appearance: BuddyAppearance,
    val equippedItems: Map<ItemCategory, String>
)

@Serializable
data class DisplayStats(
    val hunger: Float,
    val energy: Float,
    val happiness: Float,
    val stress: Float,
    val health: Float,
    val motivation: Float,
    val burnout: Float
)

@Serializable
data class DisplayWorld(
    val timeLabel: String,
    val weatherIcon: String,
    val temperatureLabel: String
)

@Serializable
data class DisplayBuff(
    val id: String,
    val label: String,
    val icon: String,
    val progress: Float // 0 to 1
)
