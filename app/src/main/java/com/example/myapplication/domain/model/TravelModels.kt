package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class District {
    RESIDENTIAL,
    COMMERCIAL,
    INDUSTRIAL,
    SLUMS,
    UPTOWN,
    DOWNTOWN
}

@Serializable
data class DistrictTravelInfo(
    val from: District,
    val to: District,
    val durationHours: Float,
    val baseCost: Int,
    val energyCost: Float,
    val stressGain: Float
)

object TravelRegistry {
    val travelMatrix = mapOf(
        (District.RESIDENTIAL to District.COMMERCIAL) to DistrictTravelInfo(District.RESIDENTIAL, District.COMMERCIAL, 0.5f, 10, 5f, 2f),
        (District.RESIDENTIAL to District.INDUSTRIAL) to DistrictTravelInfo(District.RESIDENTIAL, District.INDUSTRIAL, 1.0f, 20, 10f, 5f),
        (District.RESIDENTIAL to District.SLUMS) to DistrictTravelInfo(District.RESIDENTIAL, District.SLUMS, 1.5f, 5, 15f, 10f),
        (District.COMMERCIAL to District.DOWNTOWN) to DistrictTravelInfo(District.COMMERCIAL, District.DOWNTOWN, 0.3f, 15, 3f, 4f),
        (District.DOWNTOWN to District.UPTOWN) to DistrictTravelInfo(District.DOWNTOWN, District.UPTOWN, 0.8f, 50, 5f, 3f)
    )

    fun getTravelInfo(from: District, to: District): DistrictTravelInfo {
        if (from == to) return DistrictTravelInfo(from, to, 0f, 0, 0f, 0f)
        return travelMatrix[from to to] ?: travelMatrix[to to from]?.copy(from = from, to = to)
            ?: DistrictTravelInfo(from, to, 1.0f, 25, 10f, 5f) // Default fallback
    }
}
