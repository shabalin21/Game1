package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BuddyAppearance(
    val hairstyle: String = "default",
    val hairColor: String = "#000000",
    val skinTone: String = "#F1C27D",
    val eyeType: String = "default",
    val eyeColor: String = "#000000",
    val eyeBrows: String = "default",
    val outfitId: String? = null
)

object AppearanceRegistry {
    val hairstyles = listOf("default", "messy", "buzz", "mohawk", "long")
    val eyeTypes = listOf("default", "sharp", "round", "cyber")
    val skinTones = listOf("#F1C27D", "#E0AC69", "#8D5524", "#C68642", "#FFDBAC")
}
