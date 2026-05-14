package com.example.myapplication.ui.screen.upgrades

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.myapplication.domain.model.UpgradeCategory
import com.example.myapplication.domain.model.UpgradeModel
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.component.CoinDisplay
import com.example.myapplication.ui.theme.*

@Composable
fun UpgradesScreen(
    onBack: () -> Unit,
    viewModel: UpgradesViewModel = hiltViewModel()
) {
    val upgrades by viewModel.upgrades.collectAsState()
    val coins by viewModel.coins.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CyberBackground(accentColor = NeonBlue)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "UPGRADES_TERMINAL",
                subtitle = "PERMANENT_ENHANCEMENTS",
                accentColor = NeonBlue,
                onBack = onBack,
                trailingContent = {
                    CoinDisplay(coins = coins)
                }
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                UpgradeCategory.entries.forEach { category ->
                    val categoryUpgrades = upgrades.filter { it.category == category }
                    if (categoryUpgrades.isNotEmpty()) {
                        item {
                            Text(
                                text = category.name.uppercase(),
                                color = getCategoryColor(category),
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp,
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        items(categoryUpgrades) { upgrade ->
                            UpgradeCard(
                                upgrade = upgrade,
                                canAfford = coins >= upgrade.getNextLevelCost(),
                                onBuy = { viewModel.buyUpgrade(upgrade.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpgradeCard(
    upgrade: UpgradeModel,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    val isMax = upgrade.isMaxLevel()
    val accentColor = getCategoryColor(upgrade.category)
    
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accentColor
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    upgrade.name.uppercase(), 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Black, 
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Surface(
                    color = accentColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "LVL ${upgrade.currentLevel}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                upgrade.description, 
                style = MaterialTheme.typography.bodySmall, 
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.05f))) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(upgrade.currentLevel.toFloat() / upgrade.maxLevel.toFloat())
                        .fillMaxHeight()
                        .background(accentColor)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onBuy,
                enabled = canAfford && !isMax,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) SurfaceDark else Color.White.copy(alpha = 0.05f),
                    contentColor = if (canAfford) NeonBlue else Color.White.copy(alpha = 0.2f),
                    disabledContainerColor = Color.White.copy(alpha = 0.05f),
                    disabledContentColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isMax) {
                    Text("MAX LEVEL", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("UPGRADE", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "💰 ${upgrade.getNextLevelCost()}", 
                            style = MaterialTheme.typography.labelLarge, 
                            fontWeight = FontWeight.Black,
                            color = if (canAfford) NeonOrange else Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryColor(category: UpgradeCategory): Color = when (category) {
    UpgradeCategory.ENERGY -> NeonBlue
    UpgradeCategory.HUNGER -> NeonOrange
    UpgradeCategory.HAPPINESS -> NeonPink
    UpgradeCategory.ECONOMY -> NeonGreen
    UpgradeCategory.SLEEP -> NeonPurple
}
