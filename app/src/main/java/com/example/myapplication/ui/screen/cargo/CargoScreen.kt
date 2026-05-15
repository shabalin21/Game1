package com.example.myapplication.ui.screen.cargo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.InventoryItem
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.component.CoinDisplay
import com.example.myapplication.ui.theme.*

@Composable
fun CargoScreen(
    viewModel: CargoViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val coins by viewModel.coins.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val petSnapshot by viewModel.petState.collectAsState()

    var selectedTab by remember { mutableStateOf(CargoTab.INVENTORY) }
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }
    var selectedItem by remember { mutableStateOf<ItemModel?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CyberBackground(accentColor = PremiumBlue)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = if (selectedTab == CargoTab.INVENTORY) "Inventory" else "Store",
                subtitle = if (selectedTab == CargoTab.INVENTORY) "Storage Unit" else "Trade Network",
                accentColor = PremiumBlue,
                onBack = onBack,
                trailingContent = {
                    CoinDisplay(coins = coins)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark.copy(alpha = 0.5f))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "Inventory",
                    icon = Icons.AutoMirrored.Filled.List,
                    isSelected = selectedTab == CargoTab.INVENTORY,
                    onClick = { selectedTab = CargoTab.INVENTORY },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Store",
                    icon = Icons.Default.ShoppingCart,
                    isSelected = selectedTab == CargoTab.MARKET,
                    onClick = { selectedTab = CargoTab.MARKET },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = if (selectedCategory == it) null else it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "TabContent"
            ) { tab ->
                when (tab) {
                    CargoTab.INVENTORY -> InventoryGrid(
                        inventory = inventory,
                        filter = selectedCategory,
                        onItemClick = { selectedItem = it },
                        onGoToMarket = { selectedTab = CargoTab.MARKET }
                    )
                    CargoTab.MARKET -> MarketGrid(
                        items = viewModel.marketItems,
                        recommended = viewModel.recommendedItems,
                        filter = selectedCategory,
                        coins = coins,
                        petSnapshot = petSnapshot,
                        onItemClick = { selectedItem = it }
                    )
                }
            }
        }

        // Item Detail Dialog
        selectedItem?.let { item ->
            val totalQuantity = inventory.filter { it.itemId == item.id }.sumOf { it.quantity }
            val isOwned = !item.isConsumable && petSnapshot?.ownedPermanentIds?.contains(item.id) == true
            
            ItemDetailDialog(
                item = item,
                quantity = totalQuantity,
                isOwned = isOwned,
                canAfford = coins >= item.price,
                actionText = if (selectedTab == CargoTab.INVENTORY) "Activate" else "Purchase",
                onAction = {
                    if (selectedTab == CargoTab.INVENTORY) {
                        viewModel.processItem(item.id)
                        selectedItem = null // Close after use
                    } else {
                        viewModel.purchaseItem(item.id)
                    }
                },
                onDismiss = { selectedItem = null }
            )
        }
    }
}

@Composable
fun InventoryGrid(
    inventory: List<InventoryItem>,
    filter: ItemCategory?,
    onItemClick: (ItemModel) -> Unit,
    onGoToMarket: () -> Unit
) {
    val grouped = inventory.groupBy { it.itemId }
    val displayItems = grouped.keys
        .mapNotNull { ItemRegistry.getItem(it) }
        .filter { filter == null || it.category == filter }

    if (displayItems.isEmpty()) {
        EmptyInventoryState(onGoToMarket)
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(displayItems) { item ->
                val quantity = grouped[item.id]?.sumOf { it.quantity } ?: 0
                InventoryItemCard(
                    item = item,
                    count = quantity,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun MarketGrid(
    items: List<ItemModel>,
    recommended: List<ItemModel>,
    filter: ItemCategory?,
    coins: Int,
    petSnapshot: PetModel?,
    onItemClick: (ItemModel) -> Unit
) {
    val filteredItems = items.filter { filter == null || it.category == filter }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (filter == null) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "FEATURED DEALS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PremiumGold,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(recommended) { item ->
                val isOwned = !item.isConsumable && petSnapshot?.ownedPermanentIds?.contains(item.id) == true
                MarketItemCard(
                    item = item,
                    canAfford = coins >= item.price,
                    isOwned = isOwned,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.shadow(16.dp, ambientColor = PremiumGold, spotColor = PremiumGold)
                )
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ALL ITEMS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        items(filteredItems) { item ->
            val isOwned = !item.isConsumable && petSnapshot?.ownedPermanentIds?.contains(item.id) == true
            MarketItemCard(
                item = item,
                canAfford = coins >= item.price,
                isOwned = isOwned,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun InventoryItemCard(
    item: ItemModel,
    count: Int,
    onClick: () -> Unit
) {
    val rarityColor = getRarityColor(item.rarity)
    
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        accentColor = rarityColor,
        showGlow = item.rarity >= ItemRarity.RARE
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxWidth()) {
                CyberBadge(text = "x$count", accentColor = rarityColor)
            }

            Text(
                text = item.icon, 
                fontSize = 42.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = 0.9f
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = item.rarity.name,
                style = MaterialTheme.typography.labelSmall,
                color = rarityColor.copy(alpha = 0.6f),
                fontSize = 8.sp
            )
        }
    }
}

@Composable
fun MarketItemCard(
    item: ItemModel,
    canAfford: Boolean,
    isOwned: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColor = getRarityColor(item.rarity)
    
    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        accentColor = if (isOwned) PremiumBlue else rarityColor,
        showGlow = item.rarity >= ItemRarity.RARE || isOwned
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(contentAlignment = Alignment.TopStart, modifier = Modifier.fillMaxWidth()) {
                if (item.rarity >= ItemRarity.RARE) {
                    CyberBadge(text = item.rarity.name.take(1), accentColor = rarityColor)
                }
            }

            Text(
                text = item.icon, 
                fontSize = 42.sp,
                modifier = Modifier.graphicsLayer { 
                    alpha = if (isOwned) 0.5f else 1.0f 
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = if (isOwned) Color.White.copy(alpha = 0.5f) else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isOwned) {
                CyberBadge(text = "OWNED", accentColor = PremiumBlue)
            } else {
                Text(
                    text = "💰 ${item.price}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (canAfford) PremiumGold else PremiumRed,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(if (isSelected) SurfaceCard else Color.Transparent, label = "TabBackground")
    val contentColor by animateColorAsState(if (isSelected) PremiumCyan else Color.White.copy(alpha = 0.4f), label = "TabContent")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: ItemCategory?,
    onCategorySelected: (ItemCategory) -> Unit
) {
    val visibleCategories = listOf(
        ItemCategory.PRODUCTS, ItemCategory.CLOTHES, ItemCategory.ITEMS, ItemCategory.HOMES
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(visibleCategories) { category ->
            val isSelected = category == selectedCategory
            val color = getCategoryColor(category)
            
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCategorySelected(category) },
                color = if (isSelected) color.copy(alpha = 0.2f) else SurfaceDark,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)) else null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = category.name,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmptyInventoryState(onGoToMarket: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "EmptyState")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer { translationY = floatAnim }
                .background(Color.White.copy(alpha = 0.03f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📦", 
                fontSize = 84.sp, 
                modifier = Modifier.graphicsLayer { alpha = 0.4f }
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "STORAGE EMPTY",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            letterSpacing = 4.sp
        )
        
        Text(
            text = "Your storage bay is currently empty.\nVisit the store to acquire supplies.",
            color = Color.White.copy(alpha = 0.4f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        CyberButton(
            text = "ACCESS STORE",
            onClick = onGoToMarket,
            color = PremiumCyan
        )
    }
}

private fun getCategoryColor(category: ItemCategory): Color = when (category) {
    ItemCategory.PRODUCTS -> PremiumGreen
    ItemCategory.CLOTHES -> PremiumPink
    ItemCategory.ITEMS -> Color.Cyan
    ItemCategory.HOMES -> PremiumPurple
    else -> Color.Gray
}

private fun getRarityColor(rarity: ItemRarity): Color = when (rarity) {
    ItemRarity.COMMON -> Color(0xFF9E9E9E)
    ItemRarity.UNCOMMON -> PremiumGreen
    ItemRarity.RARE -> PremiumBlue
    ItemRarity.EPIC -> PremiumPurple
    ItemRarity.LEGENDARY -> PremiumGold
    ItemRarity.MYTHIC -> PremiumPink
    ItemRarity.GOLD -> Color(0xFFFFD700)
}

enum class CargoTab { INVENTORY, MARKET }


