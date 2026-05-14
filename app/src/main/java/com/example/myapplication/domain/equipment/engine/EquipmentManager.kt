package com.example.myapplication.domain.equipment.engine

import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentManager @Inject constructor(
    private val petRepository: PetRepository,
    private val logManager: TerminalLogManager
) {

    suspend fun equipItem(itemId: String): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        val item = ItemRegistry.getItem(itemId) ?: return false

        if (!pet.ownedPermanentIds.contains(itemId)) {
            logManager.log(LogCategory.WARNING, "UNAUTHORIZED_EQUIP: Item not owned.")
            return false
        }

        val newEquipped = pet.equippedItems.toMutableMap()
        newEquipped[item.category] = itemId

        petRepository.savePetState(pet.copy(equippedItems = newEquipped))
        logManager.log(LogCategory.SYSTEM, "EQUIP_LINK_ESTABLISHED: ${item.name}")
        return true
    }

    suspend fun unequipItem(category: ItemCategory): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        val itemId = pet.equippedItems[category] ?: return false
        val item = ItemRegistry.getItem(itemId)

        val newEquipped = pet.equippedItems.toMutableMap()
        newEquipped.remove(category)

        petRepository.savePetState(pet.copy(equippedItems = newEquipped))
        logManager.log(LogCategory.SYSTEM, "EQUIP_LINK_TERMINATED: ${item?.name ?: "Unknown"}")
        return true
    }

    suspend fun saveCurrentOutfit(name: String): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        if (pet.equippedItems.isEmpty()) return false

        val newOutfits = pet.savedOutfits.toMutableMap()
        newOutfits[name] = pet.equippedItems

        petRepository.savePetState(pet.copy(savedOutfits = newOutfits))
        logManager.log(LogCategory.SYSTEM, "OUTFIT_PRESET_SAVED: $name")
        return true
    }

    suspend fun loadOutfit(name: String): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        val outfit = pet.savedOutfits[name] ?: return false

        petRepository.savePetState(pet.copy(equippedItems = outfit))
        logManager.log(LogCategory.SYSTEM, "OUTFIT_PRESET_LOADED: $name")
        return true
    }
}
