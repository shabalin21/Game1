package com.example.myapplication.ui.fx

import java.util.ArrayDeque

/**
 * OBJECT POOL FOR PARTICLES.
 * Prevents GC pressure during high-frequency spawning.
 */
class ParticlePool(private val maxSize: Int) {
    private val pool = ArrayDeque<Particle>(maxSize)
    
    init {
        repeat(maxSize) {
            pool.add(Particle())
        }
    }

    fun obtain(): Particle? {
        val particle = if (pool.isNotEmpty()) pool.poll() else null
        particle?.isActive = true
        return particle
    }

    fun release(particle: Particle) {
        particle.reset()
        if (pool.size < maxSize) {
            pool.offer(particle)
        }
    }
}
