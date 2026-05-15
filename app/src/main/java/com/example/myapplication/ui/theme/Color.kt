package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

// GLOBAL CORE BACKGROUNDS
val BackgroundDark = Color(0xFF0F1117)
val SurfaceDark = Color(0xFF161A22)
val SurfaceLayer = Color(0xFF1F2430)
val SurfaceCard = Color(0xFF262C3D).copy(alpha = 0.8f)

// PREMIUM ACCENT SYSTEM (Soft Gradients & Subtle Glow)
val PremiumBlue = Color(0xFF4FC3F7)    // Analytics / Data / Inventory
val PremiumPurple = Color(0xFFB39DDB)  // System / Upgrades / Lab
val PremiumGreen = Color(0xFF81C784)   // Health / Stability / Positive
val PremiumRed = Color(0xFFEF5350)     // Danger / Warnings / Stress
val PremiumGold = Color(0xFFFFD54F)    // Economy / Assets
val PremiumPink = Color(0xFFF06292)    // Buddy / Emotions
val PremiumOrange = Color(0xFFFF8A65)  // Energy / Heat / Special
val PremiumCyan = Color(0xFF4DD0E1)    // Tech / Digital / Cool

// STATUS COLORS
val StatHunger = PremiumGold
val StatEnergy = PremiumBlue
val StatHealth = PremiumGreen
val StatHappiness = PremiumPink
val StatSocial = PremiumPurple

// DESIGN SYSTEM SPECIFICS
val GlassBackground = Color(0x0DFFFFFF) // Reduced opacity for more subtle glass effect
val GlassBorder = Color(0x1AFFFFFF)     // More subtle borders
val GlassBorderStrong = Color(0x33FFFFFF)

val PrimaryColor = PremiumPurple
val SecondaryColor = PremiumBlue
val AccentColor = PremiumPink
