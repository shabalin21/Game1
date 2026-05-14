package com.example.myapplication.ui.fx

import androidx.compose.ui.graphics.Color

/**
 * PRODUCTION-GRADE PARTICLE MODEL.
 * Mutable for performance within the pool.
 */
class Particle {
    var x: Float = 0f
    var y: Float = 0f
    var vx: Float = 0f
    var vy: Float = 0f
    var life: Float = 0f
    var maxLife: Float = 1f
    var color: Color = Color.White
    var scale: Float = 1f
    var alpha: Float = 1f
    var rotation: Float = 0f
    var type: ParticleType = ParticleType.SPARKLE
    var isActive: Boolean = false

    fun reset() {
        x = 0f
        y = 0f
        vx = 0f
        vy = 0f
        life = 0f
        maxLife = 1f
        color = Color.White
        scale = 1f
        alpha = 1f
        rotation = 0f
        type = ParticleType.SPARKLE
        isActive = false
    }

    fun update(deltaTime: Float) {
        if (!isActive) return
        
        life -= deltaTime
        if (life <= 0) {
            isActive = false
            return
        }

        x += vx * deltaTime
        y += vy * deltaTime
        
        // Simple alpha fade out
        alpha = (life / maxLife).coerceIn(0f, 1f)
    }
}

enum class ParticleType {
    SPARKLE, SMOKE, HEART, ZZZ, DUST, RAIN, SNOW, BUBBLE, GLITCH
}
