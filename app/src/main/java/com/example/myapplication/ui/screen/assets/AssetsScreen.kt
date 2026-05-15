package com.example.myapplication.ui.screen.assets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.market.AssetPrice
import com.example.myapplication.domain.market.AssetType
import com.example.myapplication.domain.model.LifetimeStats
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.component.CoinDisplay
import com.example.myapplication.ui.theme.*

@Composable
fun AssetsScreen(
    viewModel: AssetsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val assets by viewModel.assets.collectAsState()
    val portfolio by viewModel.portfolio.collectAsState()
    val costBasis by viewModel.costBasis.collectAsState()
    val stats by viewModel.statistics.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val portfolioValue by viewModel.portfolioValue.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CyberBackground(accentColor = PremiumGold)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            ScreenHeader(
                title = "STOCK MARKET",
                subtitle = "PORTFOLIO: ${portfolioValue.toInt()} CR",
                accentColor = PremiumGold,
                onBack = onBack,
                trailingContent = {
                    CoinDisplay(coins = coins)
                }
            )

            // Statistics Overview
            MarketStatisticsSection(stats = stats, portfolioValue = portfolioValue)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ACTIVE MARKETS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Asset List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(assets) { asset ->
                    val owned = portfolio[asset.id] ?: 0f
                    val basis = costBasis[asset.id] ?: 0f
                    
                    AssetCard(
                        asset = asset,
                        owned = owned,
                        costBasis = basis,
                        onBuy = { viewModel.buyAsset(asset.id, 1f) },
                        onSell = { viewModel.sellAsset(asset.id, 1f) }
                    )
                }
            }
        }
    }
}

@Composable
fun MarketStatisticsSection(stats: LifetimeStats, portfolioValue: Float) {
    CyberCard(accentColor = PremiumBlue.copy(alpha = 0.5f)) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("TOTAL INVESTED", "${stats.totalInvested}", PremiumBlue)
                StatItem("TOTAL PROFIT", "${stats.totalMarketProfit}", PremiumGreen)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("TOTAL LOSSES", "${stats.totalMarketLosses}", PremiumRed)
                StatItem("TOTAL TRADES", "${stats.totalTrades}", PremiumGold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("BEST TRADE", "${stats.bestTrade}", PremiumGreen)
                StatItem("WORST TRADE", "${stats.worstTrade}", PremiumRed)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Black)
    }
}

@Composable
fun AssetCard(
    asset: AssetPrice,
    owned: Float,
    costBasis: Float,
    onBuy: () -> Unit,
    onSell: () -> Unit
) {
    val accentColor = when (asset.type) {
        AssetType.CRYPTO -> PremiumGold
        AssetType.STOCK -> PremiumBlue
        AssetType.SECURITIES -> PremiumGreen
        AssetType.GOLD -> Color(0xFFFFD700)
        AssetType.SILVER -> Color(0xFFC0C0C0)
        AssetType.CASH -> PremiumCyan
    }

    val currentValue = owned * asset.currentPrice
    val profit = currentValue - costBasis
    val profitColor = if (profit >= 0) PremiumGreen else PremiumRed

    CyberCard(accentColor = accentColor) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = asset.type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${asset.currentPrice.toInt()} CR",
                        style = MaterialTheme.typography.titleMedium,
                        color = PremiumGold,
                        fontWeight = FontWeight.Black
                    )
                    val trend = if (asset.history.size > 1) {
                        val last = asset.history.last()
                        val prev = asset.history[asset.history.size - 2]
                        if (last > prev) "▲" else "▼"
                    } else ""
                    Text(
                        text = trend,
                        color = if (trend == "▲") PremiumGreen else PremiumRed,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Asset Chart
            BTCChartComponent(
                history = asset.history,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                accentColor = accentColor
            )

            if (owned > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("OWNED", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                        Text("${owned.toInt()} UNITS", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("COST BASIS", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                        Text("${costBasis.toInt()} CR", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("PROFIT/LOSS", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                        Text("${if (profit >= 0) "+" else ""}${profit.toInt()} CR", style = MaterialTheme.typography.labelMedium, color = profitColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CyberButton(
                    text = "BUY",
                    onClick = onBuy,
                    color = PremiumGreen,
                    modifier = Modifier.weight(1f)
                )
                if (owned > 0) {
                    CyberButton(
                        text = "SELL",
                        onClick = onSell,
                        color = PremiumRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


