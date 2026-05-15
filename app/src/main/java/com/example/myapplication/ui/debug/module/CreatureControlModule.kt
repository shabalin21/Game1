package com.example.myapplication.ui.debug.module

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.ui.debug.DevLabManager
import com.example.myapplication.ui.theme.PremiumPurple

@Composable
fun CreatureControlModule(
    pet: PetModel,
    manager: DevLabManager,
    onUpdateStat: (String, Float) -> Unit
) {
    val stats = listOf(
        "Hunger" to pet.stats.hunger,
        "Energy" to pet.stats.energy,
        "Happiness" to pet.stats.happiness,
        "Health" to pet.stats.health,
        "Hygiene" to pet.stats.hygiene,
        "Social" to pet.stats.social,
        "Stress" to pet.stats.stress
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("CURRENT INTENT", fontSize = 10.sp, color = PremiumPurple, fontWeight = FontWeight.Black)
                    Text(pet.psychology.currentActivity.name, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(stats) { (name, value) ->
            StatSlider(
                name = name,
                value = value,
                isFrozen = manager.isStatFrozen(name),
                onValueChange = { onUpdateStat(name, it) },
                onFreezeToggle = { manager.toggleFreezeStat(name, value) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun StatSlider(
    name: String,
    value: Float,
    isFrozen: Boolean,
    onValueChange: (Float) -> Unit,
    onFreezeToggle: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    String.format("%.1f", value),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFrozen) Color.Cyan else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
                
                IconButton(
                    onClick = onFreezeToggle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Freeze",
                        tint = if (isFrozen) Color.Cyan else Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = if (isFrozen) Color.Cyan else PremiumPurple,
                activeTrackColor = if (isFrozen) Color.Cyan.copy(alpha = 0.5f) else PremiumPurple.copy(alpha = 0.5f),
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            ),
            modifier = Modifier.height(24.dp)
        )
    }
}

