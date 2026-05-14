package com.example.myapplication.ui.screen.buddy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.equipment.engine.EquipmentManager
import com.example.myapplication.domain.model.ItemCategory
import com.example.myapplication.domain.model.ItemRegistry
import com.example.myapplication.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.myapplication.domain.model.BuddyAppearance
import kotlinx.coroutines.flow.first

@HiltViewModel
class BuddyViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val equipmentManager: EquipmentManager
) : ViewModel() {

    val petState = petRepository.getPetState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateAppearance(appearance: BuddyAppearance) {
        viewModelScope.launch {
            val pet = petRepository.getPetState().first() ?: return@launch
            petRepository.savePetState(pet.copy(appearance = appearance))
        }
    }

    val ownedWearables = petRepository.getPetState().map { pet ->
        pet?.ownedPermanentIds?.mapNotNull { ItemRegistry.getItem(it) }
            ?.filter { isWearable(it.category) } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun isWearable(category: ItemCategory): Boolean {
        return category == ItemCategory.HEAD || 
               category == ItemCategory.TOP || 
               category == ItemCategory.BOTTOM || 
               category == ItemCategory.SHOES || 
               category == ItemCategory.ACCESSORY
    }

    fun equip(itemId: String) {
        viewModelScope.launch {
            equipmentManager.equipItem(itemId)
        }
    }

    fun unequip(category: ItemCategory) {
        viewModelScope.launch {
            equipmentManager.unequipItem(category)
        }
    }

    fun saveOutfit(name: String) {
        viewModelScope.launch {
            equipmentManager.saveCurrentOutfit(name)
        }
    }

    fun loadOutfit(name: String) {
        viewModelScope.launch {
            equipmentManager.loadOutfit(name)
        }
    }
}
