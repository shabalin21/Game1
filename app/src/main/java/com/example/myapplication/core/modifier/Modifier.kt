package com.example.myapplication.core.modifier

import kotlinx.serialization.Serializable

/**
 * A systemic modifier that can be applied to any stat or value.
 */
@Serializable
data class Modifier(
    val id: String,
    val name: String,
    val value: Float,
    val type: ModifierType,
    val source: ModifierSource,
    val expirationTimestamp: Long? = null, // null for permanent modifiers
    val tag: String? = null // Optional tag for granular filtering
) {
    val isExpired: Boolean
        get() = expirationTimestamp != null && System.currentTimeMillis() > expirationTimestamp
}
