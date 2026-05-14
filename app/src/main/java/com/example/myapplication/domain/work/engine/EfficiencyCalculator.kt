package com.example.myapplication.domain.work.engine

import com.example.myapplication.domain.model.PetModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EfficiencyCalculator @Inject constructor() {

    fun calculateEfficiency(pet: PetModel): Float {
        val stats = pet.stats
        
        // Base factors
        val energyFactor = (stats.energy / 100f).coerceIn(0.2f, 1.0f)
        val stressFactor = (1.0f - (stats.stress / 100f)).coerceIn(0.1f, 1.0f)
        val disciplineFactor = (0.5f + (stats.discipline / 200f)).coerceIn(0.5f, 1.0f)
        val happinessFactor = (0.8f + (stats.happiness / 500f)).coerceIn(0.8f, 1.0f)

        // Calculate combined efficiency
        var efficiency = 100f * energyFactor * stressFactor * disciplineFactor * happinessFactor
        
        // Bonus for intelligence
        efficiency += (stats.intelligence / 10f)

        return efficiency.coerceIn(0f, 150f)
    }
}
