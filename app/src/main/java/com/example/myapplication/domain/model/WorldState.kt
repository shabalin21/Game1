package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WorldState(
    val timeOfDay: TimeOfDay = TimeOfDay.DAY,
    val weather: Weather = Weather.SUNNY,
    val temperature: Float = 22f, // Celsius
    val cleanliness: Float = 100f, // 0 to 100
    val lastUpdateTimestamp: Long = System.currentTimeMillis()
)

@Serializable
enum class TimeOfDay {
    DAWN, DAY, DUSK, NIGHT
}

@Serializable
enum class Weather {
    SUNNY, RAINY, CLOUDY, STORMY
}
