package com.example.myapplication.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.domain.model.ItemModel
import com.example.myapplication.domain.model.ItemRarity
import com.example.myapplication.domain.model.StatEffect
import com.example.myapplication.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ItemDetailDialog(
    item: ItemModel,
    quantity: Int,
    isOwned: Boolean,
    canAfford: Boolean,
    actionText: String,
    onAction: () -> Unit,
    onDismiss: () -> Unit
) {
    val rarityColor = getRarityColor(item.rarity)
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                borderColor = rarityColor,
                showGlow = true
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.5f))
                        }
                    }

                    // Item Icon with Glow
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(24.dp, CircleShape, ambientColor = rarityColor, spotColor = rarityColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(2.dp, rarityColor.copy(alpha = 0.5f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = item.icon, fontSize = 56.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Name & Rarity
                    Text(
                        text = item.name.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Surface(
                        color = rarityColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, rarityColor.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = item.rarity.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = rarityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats Effects
                    StatEffectGrid(effect = item.effect, sideEffect = item.sideEffect)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (item.isConsumable) "QUANTITY" else "STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                            Text(
                                text = if (item.isConsumable) "x$quantity" else if (isOwned) "OWNED" else "UNLOCKED",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isOwned) NeonBlue else Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }

                        CyberButton(
                            text = if (actionText == "PURCHASE") "BUY 💰${item.price}" else actionText,
                            onClick = onAction,
                            color = if (actionText == "PURCHASE") (if (canAfford) NeonOrange else Color.Red) else NeonCyan,
                            enabled = if (actionText == "PURCHASE") {
                                canAfford && !isOwned
                            } else if (actionText == "OWNED") {
                                false
                            } else {
                                quantity > 0 || !item.isConsumable
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatEffectGrid(effect: StatEffect, sideEffect: StatEffect?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Text(
            text = "SYSTEM IMPACT",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val effects = listOf(
            Triple("HUNGER", effect.hungerChange, NeonGreen),
            Triple("ENERGY", effect.energyChange, NeonBlue),
            Triple("HAPPINESS", effect.happinessChange, NeonOrange),
            Triple("HEALTH", effect.healthChange, Color.Red),
            Triple("STRESS", effect.stressChange, NeonPink),
            Triple("INTELLIGENCE", effect.intelligenceChange, NeonPurple)
        ).filter { it.second != 0f }

        if (effects.isEmpty()) {
            Text(text = "NO DIRECT STAT IMPACT", color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)
        } else {
            effects.forEach { (label, value, color) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${if (value > 0) "+" else ""}${value.roundToInt()}%",
                        color = if (value > 0) color else Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        if (sideEffect != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "SIDE EFFECTS (UPON EXPIRY)",
                style = MaterialTheme.typography.labelSmall,
                color = NeonPink.copy(alpha = 0.7f),
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val sides = listOf(
                Triple("ENERGY", sideEffect.energyChange, NeonBlue),
                Triple("STRESS", sideEffect.stressChange, NeonPink)
            ).filter { it.second != 0f }
            
            sides.forEach { (label, value, color) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    Text(
                        text = "${if (value > 0) "+" else ""}${value.roundToInt()}%",
                        color = if (value > 0) color else Color.Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
