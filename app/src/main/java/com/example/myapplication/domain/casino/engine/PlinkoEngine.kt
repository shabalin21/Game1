package com.example.myapplication.domain.casino.engine

import com.example.myapplication.domain.casino.model.PlinkoBall
import com.example.myapplication.domain.casino.model.PlinkoRisk
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*
import kotlin.random.Random

@Singleton
class PlinkoEngine @Inject constructor() {
    
    val rowCount = 10
    private val gravity = 0.015f
    private val bounce = 0.55f
    private val friction = 0.995f
    private val pegRadius = 0.15f
    private val ballRadius = 0.12f
    private val collisionDist = pegRadius + ballRadius

    fun getMultipliers(risk: PlinkoRisk): List<Float> {
        return when (risk) {
            PlinkoRisk.LOW -> listOf(5.0f, 2.0f, 1.5f, 1.2f, 1.1f, 1.0f, 1.1f, 1.2f, 1.5f, 2.0f, 5.0f)
            PlinkoRisk.MEDIUM -> listOf(15.0f, 10.0f, 5.0f, 2.0f, 1.0f, 0.5f, 1.0f, 2.0f, 5.0f, 10.0f, 15.0f)
            PlinkoRisk.HIGH -> listOf(50.0f, 25.0f, 10.0f, 3.0f, 0.5f, 0.0f, 0.5f, 3.0f, 10.0f, 25.0f, 50.0f)
        }
    }

    fun updateBall(ball: PlinkoBall): PlinkoBall {
        if (ball.isFinished) return ball

        var nvx = ball.vx * friction
        var nvy = (ball.vy + gravity) * friction
        var nx = ball.x + nvx
        var ny = ball.y + nvy

        // Peg collisions
        for (row in 0 until rowCount) {
            val pegY = row.toFloat() + 1f
            val pegsInRow = row + 3
            val startX = -(pegsInRow - 1) * 0.5f
            
            for (i in 0 until pegsInRow) {
                val pegX = startX + i
                val dx = nx - pegX
                val dy = ny - pegY
                val dist = sqrt(dx * dx + dy * dy)
                
                if (dist < collisionDist) {
                    val angle = atan2(dy, dx)
                    val overlap = collisionDist - dist
                    
                    // Reflect velocity
                    val normalX = cos(angle)
                    val normalY = sin(angle)
                    val dot = nvx * normalX + nvy * normalY
                    
                    nvx = (nvx - 2 * dot * normalX) * bounce + (Random.nextFloat() - 0.5f) * 0.02f
                    nvy = (nvy - 2 * dot * normalY) * bounce + 0.01f // Add small downward kick
                    
                    nx += normalX * overlap
                    ny += normalY * overlap
                }
            }
        }

        // Horizontal Bounds
        val maxW = (rowCount + 3) * 0.5f
        if (abs(nx) > maxW) {
            nx = if (nx > 0) maxW else -maxW
            nvx = -nvx * bounce
        }

        // Finish check
        if (ny > rowCount + 1.5f) {
            return ball.copy(x = nx, y = ny, vx = nvx, vy = nvy, isFinished = true)
        }

        return ball.copy(x = nx, y = ny, vx = nvx, vy = nvy)
    }

    fun calculateResultIndex(finalX: Float): Int {
        val multipliers = 11 // Count from getMultipliers
        // With rowCount = 10, the last row has 12 pegs.
        // Pegs are from -5.5 to 5.5. Total width = 11.
        val normalized = (finalX + 5.5f) / 11f
        return floor(normalized * multipliers).toInt().coerceIn(0, multipliers - 1)
    }
}
