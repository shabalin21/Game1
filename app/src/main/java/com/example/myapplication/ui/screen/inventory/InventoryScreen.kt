package com.example.myapplication.ui.screen.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.*
import com.example.myapplication.ui.component.GlassCard
import com.example.myapplication.ui.component.ItemDetailDialog
import com.example.myapplication.ui.theme.*

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val inventory by viewModel.inventoryItems.collectAsState()
    var selectedItemId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "STORAGE",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "Items and permanent collectibles",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )

            if (inventory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Storage is currently empty", 
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.15f)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Group by itemId for display if they are identical (simple consumables)
                    // But for GOLD items, we might want to show them individually?
                    // For now, let's keep it simple and group.
                    val grouped = inventory.groupBy { it.itemId }
                    
                    items(grouped.keys.toList()) { itemId ->
                        val itemsOfType = grouped[itemId] ?: emptyList()
                        val totalQuantity = itemsOfType.sumOf { it.quantity }
                        
                        InventoryItemCard(
                            itemId = itemId, 
                            quantity = totalQuantity,
                            onClick = { selectedItemId = itemId }
                        )
                    }
                }
            }
        }

        // Details Popup
        selectedItemId?.let { id ->
            val item = ItemRegistry.getItem(id)
            if (item != null) {
                val totalQuantity = inventory.filter { it.itemId == id }.sumOf { it.quantity }
                ItemDetailDialog(
                    item = item,
                    quantity = totalQuantity,
                    isOwned = true,
                    canAfford = true,
                    actionText = if (item.isConsumable) "USE ITEM" else "EQUIP",
                    onAction = { viewModel.useItem(id) },
                    onDismiss = { selectedItemId = null }
                )
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    itemId: String, 
    quantity: Int, 
    onClick: () -> Unit
) {
    val item = ItemRegistry.getItem(itemId)
    val rarityColor = item?.let { getRarityColor(it.rarity) } ?: NeonPurple

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        borderColor = rarityColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item?.icon ?: "📦", fontSize = 34.sp)
                
                if (quantity > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(NeonPurple, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "x$quantity",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "INFO", 
                style = MaterialTheme.typography.labelSmall, 
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.3f),
                letterSpacing = 1.sp
            )
        }
    }
}

private fun getRarityColor(rarity: ItemRarity): Color = when (rarity) {
    ItemRarity.COMMON -> Color(0xFF9E9E9E)
    ItemRarity.UNCOMMON -> CyberGreen
    ItemRarity.RARE -> CyberBlue
    ItemRarity.EPIC -> CyberPurple
    ItemRarity.LEGENDARY -> CyberYellow
    ItemRarity.MYTHIC -> NeonPink
    ItemRarity.GOLD -> Color(0xFFFFD700)
}
