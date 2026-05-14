package com.example.myapplication.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.domain.model.PetModel

@Composable
fun PetView(
    pet: PetModel,
    modifier: Modifier = Modifier
) {
    PetSprite(
        emotion = pet.emotionState,
        psychology = pet.psychology,
        isSleeping = pet.isSleeping,
        modifier = modifier
    )
}
