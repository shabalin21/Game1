package com.example.myapplication.ui.screen.ownership

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.myapplication.domain.model.ItemCategory
import com.example.myapplication.domain.model.ItemModel
import com.example.myapplication.domain.model.ItemRegistry
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.screen.buddy.BuddyViewModel
import com.example.myapplication.ui.theme.*

@Composable
fun OwnershipGalleryScreen(
    viewModel: BuddyViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val pet by viewModel.petState.collectAsState()
    val ownedIds = pet?.ownedPermanentIds ?: emptySet()
    val equipped = pet?.equippedItems ?: emptyMap()
    
    val ownedItems = ownedIds.mapNotNull { ItemRegistry.getItem(it) }
    
    val homes = ownedItems.filter { it.category == ItemCategory.HOMES }
    val vehicles = ownedItems.filter { it.category == ItemCategory.VEHICLES }
    val jewelry = ownedItems.filter { it.category == ItemCategory.JEWELRY }
    val pets = ownedItems.filter { it.category == ItemCategory.PETS }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CyberBackground(accentColor = PremiumBlue)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            ScreenHeader(
                title = "COLLECTION_VAULT",
                subtitle = "OWNED_ASSETS",
                accentColor = PremiumBlue,
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                if (homes.isNotEmpty()) {
                    item { AssetSection("PROPERTIES", homes, equipped, ItemCategory.HOMES) { viewModel.equip(it) } }
                }
                if (vehicles.isNotEmpty()) {
                    item { AssetSection("GARAGE", vehicles, equipped, ItemCategory.VEHICLES) { viewModel.equip(it) } }
                }
                if (jewelry.isNotEmpty()) {
                    item { AssetSection("VAULT", jewelry, equipped, ItemCategory.JEWELRY) { viewModel.equip(it) } }
                }
                if (pets.isNotEmpty()) {
                    item { AssetSection("PETS", pets, equipped, ItemCategory.PETS) { viewModel.equip(it) } }
                }
                
                if (ownedItems.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 100.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "NO PERMANENT ASSETS OWNED",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.3f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssetSection(
    title: String,
    items: List<ItemModel>,
    equipped: Map<ItemCategory, String>,
    category: ItemCategory,
    onEquip: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { item ->
                val isEquipped = equipped[category] == item.id
                OwnershipCard(item, isEquipped) { onEquip(item.id) }
            }
        }
    }
}

@Composable
fun OwnershipCard(item: ItemModel, isEquipped: Boolean, onEquip: () -> Unit) {
    val accentColor = when(item.category) {
        ItemCategory.HOMES -> PremiumPurple
        ItemCategory.VEHICLES -> PremiumBlue
        ItemCategory.JEWELRY -> PremiumGold
        ItemCategory.PETS -> PremiumGreen
        else -> Color.White
    }

    CyberCard(
        modifier = Modifier
            .size(width = 140.dp, height = 180.dp)
            .clickable { onEquip() },
        accentColor = if (isEquipped) accentColor else Color.White.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.icon, fontSize = 40.sp)
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.name.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 2
                )
                
                if (isEquipped) {
                    Spacer(modifier = Modifier.height(4.dp))
                    CyberBadge(text = "ACTIVE", accentColor = accentColor)
                } else {
                    Text(
                        text = "EQUIP",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

