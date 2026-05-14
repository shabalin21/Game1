package com.example.myapplication.domain.progression

import com.example.myapplication.domain.model.EvolutionStage
import com.example.myapplication.domain.model.PetModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressionSystem @Inject constructor() {

    fun addExperience(pet: PetModel, amount: Long): PetModel {
        val newXp = pet.xp + amount
        val xpToLevel = calculateXpToLevel(pet.level)
        
        return if (newXp >= xpToLevel) {
            val leveledPet = pet.copy(
                level = pet.level + 1,
                xp = newXp - xpToLevel
            )
            checkEvolution(leveledPet)
        } else {
            pet.copy(xp = newXp)
        }
    }

    private fun calculateXpToLevel(level: Int): Long {
        return (level * 100).toLong()
    }

    private fun checkEvolution(pet: PetModel): PetModel {
        val nextStage = when {
            pet.level >= 50 && pet.evolutionStage == EvolutionStage.ADULT -> EvolutionStage.SENIOR
            pet.level >= 20 && pet.evolutionStage == EvolutionStage.TEEN -> EvolutionStage.ADULT
            pet.level >= 10 && pet.evolutionStage == EvolutionStage.CHILD -> EvolutionStage.TEEN
            pet.level >= 5 && pet.evolutionStage == EvolutionStage.BABY -> EvolutionStage.CHILD
            else -> pet.evolutionStage
        }
        return pet.copy(evolutionStage = nextStage)
    }
}
