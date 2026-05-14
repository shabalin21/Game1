package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.domain.model.WorldState
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun getPetState(): Flow<PetModel?>
    suspend fun ensurePetExists()
    suspend fun savePetState(pet: PetModel)
}
