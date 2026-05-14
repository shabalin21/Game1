package com.example.myapplication.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object District : Screen

    @Serializable
    data object Inventory : Screen

    @Serializable
    data object Property : Screen

    @Serializable
    data object Social : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Settings : Screen

    // --- SUB-SCREENS ---
    
    // District Sub-pages
    @Serializable
    data object Casino : Screen
    @Serializable
    data object Work : Screen
    @Serializable
    data object Assets : Screen
    @Serializable
    data object SpecialShops : Screen
    @Serializable
    data object SpecialtyExchange : Screen
    @Serializable
    data object ActivityHub : Screen

    // Inventory Sub-pages
    @Serializable
    data object Buddy : Screen
    @Serializable
    data object Storage : Screen

    // Property Sub-pages
    @Serializable
    data object Ownership : Screen
    @Serializable
    data object Upgrades : Screen

    // Social Sub-pages
    @Serializable
    data object SocialFeed : Screen
    @Serializable
    data object Missions : Screen

    // Profile Sub-pages
    @Serializable
    data object Statistics : Screen
    @Serializable
    data object Prestige : Screen
    @Serializable
    data object Debug : Screen

    // Simulation Activities (Minigames)
    @Serializable
    data object TapRush : Screen
    @Serializable
    data object MemoryMatch : Screen
    @Serializable
    data object ReactionTap : Screen
    @Serializable
    data object FocusDodge : Screen
    @Serializable
    data object SystemSync : Screen
    @Serializable
    data object KineticPath : Screen

    // Legacy / To be moved
    @Serializable
    data object WellnessCenter : Screen

    @Serializable
    data object Store : Screen
}
