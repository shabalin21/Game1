package com.example.myapplication.data.repository

import com.example.myapplication.data.local.EconomyEntity
import com.example.myapplication.data.local.PetDao
import com.example.myapplication.data.local.toDomain
import com.example.myapplication.data.local.toEntity
import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.domain.repository.PetRepository
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val petDao: PetDao,
    private val json: Json
) : PetRepository {

    override fun getPetState(): Flow<PetModel?> {
        return petDao.getPetState().map { entity ->
            try {
                entity?.toDomain(json)
            } catch (e: Exception) {
                Timber.e(e, "Critical error decoding pet state")
                null
            }
        }
    }

    override suspend fun ensurePetExists() {
        val pet = petDao.getPetState().take(1).firstOrNull()
        if (pet == null) {
            Timber.i("Pet not found, creating initial pet...")
            val initialPet = PetModel(name = "Buddy")
            savePetState(initialPet)
            
            // Also ensure economy exists
            val economy = petDao.getEconomy().take(1).firstOrNull()
            if (economy == null) {
                petDao.updateEconomy(EconomyEntity())
            }
        }
    }

    override suspend fun savePetState(pet: PetModel) {
        petDao.insertPet(pet.toEntity(json))
    }
}
