package com.example.myapplication.ui.screen.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.ItemModel
import com.example.myapplication.domain.repository.InventoryItem
import com.example.myapplication.domain.repository.InventoryRepository
import com.example.myapplication.domain.simulation.SimulationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val simulationManager: SimulationManager
) : ViewModel() {

    val inventoryItems: StateFlow<List<InventoryItem>> = inventoryRepository.getInventory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun useItem(itemId: String) {
        viewModelScope.launch {
            simulationManager.useItem(itemId)
        }
    }
}
