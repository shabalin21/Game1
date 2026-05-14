package com.example.myapplication.ui.screen.buddy

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.ItemCategory
import com.example.myapplication.domain.model.ItemModel
import com.example.myapplication.domain.model.ItemRegistry
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.myapplication.domain.model.BuddyAppearance

@Composable
fun BuddyScreen(
    viewModel: BuddyViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val pet by viewModel.petState.collectAsState()
    val wearables by viewModel.ownedWearables.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PremiumBackground(accentColor = PremiumPink) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                title = "Wardrobe",
                subtitle = "Appearance",
                accentColor = PremiumPink,
                onBack = onBack,
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )

            // Buddy Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                pet?.let {
                    BuddyVisualPreview(it.equippedItems, it.appearance)
                }
            }

            // Scrollable Editor & Inventory
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = Shapes.cardRadius, topEnd = Shapes.cardRadius))
                    .background(SurfaceDark.copy(alpha = 0.9f))
                    .border(1.dp, GlassBorder, RoundedCornerShape(topStart = Shapes.cardRadius, topEnd = Shapes.cardRadius))
            ) {
                Box(modifier = Modifier.height(350.dp)) {
                    val scrollState = rememberScrollState()
                    Column(modifier = Modifier.verticalScroll(scrollState).padding(Spacing.lg)) {
                        
                        pet?.let {
                            BuddyAppearanceEditor(
                                appearance = it.appearance,
                                onUpdateAppearance = { newApp -> viewModel.updateAppearance(newApp) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.lg))

                        EquipmentInterface(
                            wearables = wearables,
                            equipped = pet?.equippedItems ?: emptyMap(),
                            onEquip = { viewModel.equip(it) },
                            onUnequip = { viewModel.unequip(it) }
                        )

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        // Outfits Section
                        OutfitSection(
                            outfits = pet?.savedOutfits ?: emptyMap(),
                            onSave = { viewModel.saveOutfit("STYLE_${(pet?.savedOutfits?.size ?: 0) + 1}") },
                            onLoad = { viewModel.loadOutfit(it) }
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.xl))
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitSection(
    outfits: Map<String, Map<ItemCategory, String>>,
    onSave: () -> Unit,
    onLoad: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("STORED OUTFITS", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            TextButton(onClick = onSave) {
                Text("SAVE CURRENT", color = PremiumPink, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            items(outfits.keys.toList()) { name ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Shapes.buttonRadius))
                        .clickable { onLoad(name) },
                    color = GlassBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun BuddyVisualPreview(
    equipped: Map<ItemCategory, String>,
    appearance: BuddyAppearance = BuddyAppearance()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BuddyBounce")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bounce"
    )

    Box(contentAlignment = Alignment.Center) {
        // Soft Circular Glow Effect - REWORKED
        Box(
            modifier = Modifier
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PremiumPink.copy(alpha = 0.12f),
                            PremiumPink.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = bounce.dp)
        ) {
            // HEAD SLOT (Hats/Hair)
            Box(contentAlignment = Alignment.TopCenter) {
                // Hair
                Text(
                    text = when(appearance.hairstyle) {
                        "messy" -> "🦱"
                        "mohawk" -> "🔥"
                        "long" -> "💇"
                        else -> "💇‍♂️"
                    },
                    fontSize = 45.sp,
                    color = try { Color(android.graphics.Color.parseColor(appearance.hairColor)) } catch (e: Exception) { Color.Black },
                    modifier = Modifier.alpha(0.5f) // Transparency fixed
                )
                
                val headIcon = equipped[ItemCategory.HEAD]?.let { ItemRegistry.getItem(it)?.icon } ?: ""
                if (headIcon.isNotEmpty()) {
                    Text(headIcon, fontSize = 40.sp, modifier = Modifier.offset(y = (-5).dp).alpha(0.5f))
                }
            }
            
            // BUDDY BODY (Base)
            Box(contentAlignment = Alignment.Center) {
                // Skin Tone Buddy
                Text(
                    text = "🐱", 
                    fontSize = 120.sp,
                    color = try { Color(android.graphics.Color.parseColor(appearance.skinTone)) } catch (e: Exception) { Color.White }
                )
                
                // Eyes
                Text(
                    text = when(appearance.eyeType) {
                        "sharp" -> "👁️"
                        "cyber" -> "🌐"
                        else -> "👀"
                    },
                    fontSize = 30.sp,
                    modifier = Modifier.offset(y = (-10).dp).alpha(0.5f) // Transparency fixed
                )

                // TOP SLOT (Overlay)
                val topIcon = equipped[ItemCategory.TOP]?.let { ItemRegistry.getItem(it)?.icon } ?: ""
                if (topIcon.isNotEmpty()) {
                    Text(topIcon, fontSize = 60.sp, modifier = Modifier.offset(y = 15.dp).alpha(0.5f))
                }
            }
            
            // BOTTOM SLOT
            val bottomIcon = equipped[ItemCategory.BOTTOM]?.let { ItemRegistry.getItem(it)?.icon } ?: ""
            if (bottomIcon.isNotEmpty()) {
                Text(bottomIcon, fontSize = 50.sp, modifier = Modifier.offset(y = (-20).dp).alpha(0.5f))
            }

            // SHOES SLOT
            val shoesIcon = equipped[ItemCategory.SHOES]?.let { 
                val icon = ItemRegistry.getItem(it)?.icon ?: "👟"
                "$icon $icon"
            } ?: ""
            if (shoesIcon.isNotEmpty()) {
                Text(shoesIcon, fontSize = 25.sp, modifier = Modifier.offset(y = (-30).dp).alpha(0.5f))
            }
        }
    }
}

@Composable
fun EquipmentInterface(
    wearables: List<ItemModel>,
    equipped: Map<ItemCategory, String>,
    onEquip: (String) -> Unit,
    onUnequip: (ItemCategory) -> Unit
) {
    Column {
        Text("INVENTORY", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(Spacing.md))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            items(wearables) { item ->
                val isEquipped = equipped[item.category] == item.id
                WearableItemCard(item, isEquipped) {
                    if (isEquipped) onUnequip(item.category) else onEquip(item.id)
                }
            }
        }
    }
}

@Composable
fun WearableItemCard(item: ItemModel, isEquipped: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 110.dp, height = 130.dp)
            .clip(RoundedCornerShape(Shapes.cornerRadius))
            .background(if (isEquipped) PremiumPink.copy(alpha = 0.1f) else GlassBackground)
            .border(
                1.dp, 
                if (isEquipped) PremiumPink.copy(alpha = 0.4f) else GlassBorder, 
                RoundedCornerShape(Shapes.cornerRadius)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(item.icon, fontSize = 36.sp, modifier = Modifier.alpha(0.6f))
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.labelSmall, 
                color = if (isEquipped) PremiumPink else Color.White, 
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = if (isEquipped) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
