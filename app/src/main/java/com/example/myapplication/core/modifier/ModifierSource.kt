package com.example.myapplication.core.modifier

/**
 * Defines the origin of a modifier. 
 * Allows for conflict resolution and stacking rules.
 */
enum class ModifierSource {
    TEMPORARY_EFFECT,
    ENVIRONMENTAL,
    EMOTIONAL,
    LIFESTYLE,
    EQUIPMENT,
    UPGRADE,
    WORLD_EVENT,
    TRAIT,
    CHEAT
}
