package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

/**
 * Enum representing the emotional state of the pet.
 * Mood is derived from stat combinations, personality, and environmental factors.
 */
@Serializable
enum class Mood {
    HAPPY,
    ANGRY,
    SAD,
    SLEEPY,
    SICK,
    BORED,
    LONELY,
    EXCITED,
    RELAXED
}
