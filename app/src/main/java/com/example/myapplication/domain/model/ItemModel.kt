package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemCategory {
    PRODUCTS, CLOTHES, ITEMS, HOMES, VEHICLES, JEWELRY, PETS,
    // Cosmetic Granularity
    HAIR, EYES, SKIN, AURA, ACCESSORY_HEAD, ACCESSORY_BODY,
    // Legacy mapping
    FOOD, DRINKS, MEDICAL, ELECTRONICS, GYM, GAMBLING, LUXURY, MISC,
    HEAD, TOP, BOTTOM, SHOES, ACCESSORY, FURNITURE, BOOSTER, RARE, ILLEGAL, DECORATION, WORK_ENHANCER, MEDICINE, TOY, COSMETIC
}

@Serializable
enum class ItemRarity {
    COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, GOLD
}

@Serializable
enum class BuffType {
    NONE, CAFFEINE, SUGAR_RUSH, FOCUS, RELAXATION, MEDICINE, ADRENALINE, ADDICTION
}

@Serializable
data class ItemModel(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val price: Int,
    val category: ItemCategory,
    val rarity: ItemRarity = ItemRarity.COMMON,
    val effect: StatEffect = StatEffect(),
    val durationMillis: Long = 0, // 0 for instant effects
    val buffType: BuffType = BuffType.NONE,
    val sideEffect: StatEffect? = null,
    val unlockLevel: Int = 1,
    val isConsumable: Boolean = true,
    val floatValue: Float? = null // For GOLD items: 0.9 to 1.0
)

@Serializable
data class StatEffect(
    val hungerChange: Float = 0f,
    val energyChange: Float = 0f,
    val happinessChange: Float = 0f,
    val healthChange: Float = 0f,
    val stressChange: Float = 0f,
    val socialChange: Float = 0f,
    val intelligenceChange: Float = 0f,
    val fitnessChange: Float = 0f,
    val comfortChange: Float = 0f
)
