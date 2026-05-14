package com.example.myapplication.ui.screen.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.admin.AdminNotesManager
import com.example.myapplication.domain.admin.CheatManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheatViewModel @Inject constructor(
    val cheatManager: CheatManager,
    private val adminNotesManager: AdminNotesManager
) : ViewModel() {

    val adminNotes = adminNotesManager.notes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    fun saveNotes(text: String) {
        viewModelScope.launch {
            adminNotesManager.saveNotes(text)
        }
    }
}
