package com.example.myapplication.ui.screen.shop

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.*
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.component.CoinDisplay
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val coins by viewModel.coins.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val petSnapshot by viewModel.petState.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    
    val items = remember(selectedCategory) { 
        viewModel.allItems.filter { it.category == selectedCategory } 
    }

    var selectedItem by remember { mutableStateOf<ItemModel?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.purchaseResult.collect { result ->
            result.onSuccess { msg ->
                snackbarHostState.showSnackbar(msg)
            }
            result.onFailure { err ->
                snackbarHostState.showSnackbar(err.message ?: "Purchase failed")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CyberBackground(accentColor = CyberYellow)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "CORE_STORE",
                subtitle = "EQUIPMENT_&_RESOURCES",
                accentColor = CyberYellow,
                onBack = onBack,
                trailingContent = { CoinDisplay(coins = coins) }
            )

            // CATEGORY SELECTOR (Tabs)
            ShopCategorySelector(
                selected = selectedCategory,
                onSelected = { viewModel.selectCategory(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ITEMS GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { item ->
                    val isOwned = !item.isConsumable && petSnapshot?.ownedPermanentIds?.contains(item.id) == true
                    val quantity = inventory.filter { it.itemId == item.id }.sumOf { it.quantity }
                    
                    ShopItemCard(
                        item = item,
                        canAfford = coins >= item.price,
                        isOwned = isOwned,
                        quantity = quantity,
                        onClick = { selectedItem = item }
                    )
                }
            }
        }

        // Details Popup
        selectedItem?.let { item ->
            val isOwned = !item.isConsumable && petSnapshot?.ownedPermanentIds?.contains(item.id) == true
            val quantity = inventory.filter { it.itemId == item.id }.sumOf { it.quantity }
            
            ItemDetailDialog(
                item = item,
                quantity = quantity,
                isOwned = isOwned,
                canAfford = coins >= item.price,
                actionText = if (isOwned) "OWNED" else "PURCHASE",
                onAction = { 
                    if (!isOwned) {
                        viewModel.buyItem(item)
                        selectedItem = null
                    }
                },
                onDismiss = { selectedItem = null }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
fun ShopCategorySelector(
    selected: ItemCategory,
    onSelected: (ItemCategory) -> Unit
) {
    val categories = listOf(
        ItemCategory.PRODUCTS to "🛒", 
        ItemCategory.CLOTHES to "👕", 
        ItemCategory.ITEMS to "🔧", 
        ItemCategory.HOMES to "🏠",
        ItemCategory.VEHICLES to "🚗",
        ItemCategory.JEWELRY to "💎",
        ItemCategory.PETS to "🧬"
    )
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { (category, icon) ->
            val isSelected = category == selected
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) CyberYellow.copy(alpha = 0.15f) else SurfaceDark)
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) CyberYellow else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelected(category) }
                    .padding(vertical = 12.dp)
            ) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.name.take(6),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) CyberYellow else Color.White.copy(alpha = 0.5f),
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun ShopItemCard(
    item: ItemModel, 
    canAfford: Boolean, 
    isOwned: Boolean,
    quantity: Int,
    onClick: () -> Unit
) {
    val rarityColor = getRarityColor(item.rarity)
    
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        accentColor = if (isOwned) CyberBlue else rarityColor,
        showGlow = item.rarity >= ItemRarity.RARE
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(rarityColor.copy(alpha = 0.15f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.icon, fontSize = 40.sp)
                
                if (isOwned) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(CyberBlue.copy(alpha = 0.1f))
                            .border(1.dp, CyberBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    )
                }
                
                if (item.rarity == ItemRarity.GOLD && item.floatValue != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(CyberYellow, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "%.3f".format(item.floatValue),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (isOwned) {
                CyberBadge(text = "ACQUIRED", accentColor = CyberBlue)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💰",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "${item.price}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (canAfford) CyberYellow else CyberRed,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            if (quantity > 0 && item.isConsumable) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "IN STOCK: $quantity",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberBlue,
                    fontSize = 9.sp
                )
            }
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
