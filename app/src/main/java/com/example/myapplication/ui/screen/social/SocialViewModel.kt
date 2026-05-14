package com.example.myapplication.ui.screen.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.social.SocialManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val socialManager: SocialManager
) : ViewModel() {

    val petState = petRepository.getPetState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun flex(itemId: String, itemName: String) {
        viewModelScope.launch {
            socialManager.flexPossession(itemId, itemName)
        }
    }
}
