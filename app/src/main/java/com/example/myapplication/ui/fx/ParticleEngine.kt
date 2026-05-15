package com.example.myapplication.ui.fx

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParticleEngine @Inject constructor() {
    val maxParticles = 500
    private val pool = ParticlePool(maxParticles)
    
    // Performance: Use a regular ArrayList to avoid massive recompositions on each update.
    // The Canvas will be triggered to redraw via a separate frame clock.
    val particles = ArrayList<Particle>(maxParticles)

    fun update(deltaTime: Float) {
        if (particles.isEmpty()) return

        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.update(deltaTime)
            if (!particle.isActive) {
                iterator.remove()
                pool.release(particle)
            }
        }
    }

    fun spawn(
        x: Float, y: Float,
        vx: Float = 0f, vy: Float = 0f,
        life: Float = 1f,
        color: Color = Color.White,
        scale: Float = 1f,
        type: ParticleType = ParticleType.SPARKLE
    ) {
        if (particles.size >= maxParticles) return
        
        val particle = pool.obtain() ?: return
        particle.x = x
        particle.y = y
        particle.vx = vx
        particle.vy = vy
        particle.life = life
        particle.maxLife = life
        particle.color = color
        particle.scale = scale
        particle.type = type
        particle.isActive = true
        particles.add(particle)
    }

    // Specialized Spawners
    
    fun spawnExplosion(x: Float, y: Float, color: Color, count: Int = 10) {
        repeat(count) {
            spawn(
                x = x, y = y,
                vx = (Math.random().toFloat() - 0.5f) * 0.4f,
                vy = (Math.random().toFloat() - 0.5f) * 0.4f,
                life = 0.5f + Math.random().toFloat() * 0.5f,
                color = color,
                scale = 0.5f + Math.random().toFloat() * 1.5f,
                type = ParticleType.SPARKLE
            )
        }
    }
}
