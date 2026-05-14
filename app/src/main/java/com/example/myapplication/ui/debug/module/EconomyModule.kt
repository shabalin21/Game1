package com.example.myapplication.ui.debug.module

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.debug.DevLabManager
import com.example.myapplication.ui.theme.NeonOrange

@Composable
fun EconomyModule(
    currentCoins: Int,
    manager: DevLabManager,
    onAddCoins: (Int) -> Unit,
    onSetCoins: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text("Coins: $currentCoins", color = NeonOrange, style = MaterialTheme.typography.titleMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onAddCoins(100) }, modifier = Modifier.weight(1f)) { Text("+100") }
            Button(onClick = { onAddCoins(1000) }, modifier = Modifier.weight(1f)) { Text("+1k") }
            Button(onClick = { onAddCoins(-100) }, modifier = Modifier.weight(1f)) { Text("-100") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Infinite Coins", modifier = Modifier.weight(1f), color = Color.White)
            Switch(
                checked = manager.infiniteCoins,
                onCheckedChange = { manager.updateVisualToggle("infiniteCoins", it) }
            )
        }
    }
}
