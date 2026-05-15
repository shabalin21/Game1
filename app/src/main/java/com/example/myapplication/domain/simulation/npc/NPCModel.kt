package com.example.myapplication.domain.simulation.npc

import com.example.myapplication.domain.model.Mood
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NPCModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val financialState: Float = 1000f,
    val mood: Mood = Mood.RELAXED,
    val emotionalDrift: Float = 0f,
    val currentActivity: String = "IDLE",
    val location: String = "DISTRICT_1",
    val relationships: Map<String, Float> = emptyMap() // ID to affinity
)

@Serializable
data class NPCSchedule(
    val hourlyActivities: Map<Int, String> // 0-23
)
