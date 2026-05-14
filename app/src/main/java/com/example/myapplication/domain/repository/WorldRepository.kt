package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.WorldState
import kotlinx.coroutines.flow.Flow

interface WorldRepository {
    fun getWorldState(): Flow<WorldState>
    suspend fun updateWorldState(update: (WorldState) -> WorldState)
}
