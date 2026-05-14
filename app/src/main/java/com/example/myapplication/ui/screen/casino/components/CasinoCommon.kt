package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.domain.casino.model.CasinoGameType
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*

@Composable
fun CasinoScaffold(
    coins: Int,
    title: String = "Neon Lounge",
    subtitle: String = "High Stakes Gaming",
    topBarActions: @Composable RowScope.() -> Unit = {},
    tabs: @Composable () -> Unit,
    content: @Composable () -> Unit,
    logs: @Composable () -> Unit,
    accentColor: Color = NeonYellow
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // TOP AREA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = accentColor.copy(alpha = 0.5f))
                }
                Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Black)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                topBarActions()
                Spacer(modifier = Modifier.width(16.dp))
                CoinDisplay(coins = coins)
            }
        }

        // SECOND AREA: TABS
        tabs()

        Spacer(modifier = Modifier.height(16.dp))

        // THIRD AREA: CONTENT
        Box(modifier = Modifier.weight(1f)) {
            content()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTTOM AREA: LOGS
        logs()
        
        Spacer(modifier = Modifier.padding(bottom = 16.dp).navigationBarsPadding())
    }
}

@Composable
fun CasinoGameCard(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonYellow,
    content: @Composable ColumnScope.() -> Unit
) {
    NeonCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = accentColor,
        showGlow = true
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

@Composable
fun WagerButton(
    amount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonBlue
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.2f) else SurfaceDark,
        label = "WagerBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.White.copy(alpha = 0.1f),
        label = "WagerBorder"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = amount.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Black,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun BettingControls(
    selectedWager: Int,
    onWagerChange: (Int) -> Unit,
    onAction: () -> Unit,
    actionText: String,
    enabled: Boolean,
    accentColor: Color,
    maxCoins: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(10, 50, 100, 500).forEach { amount ->
                WagerButton(
                    amount = amount,
                    isSelected = selectedWager == amount,
                    onClick = { onWagerChange(amount) },
                    modifier = Modifier.weight(1f),
                    accentColor = accentColor
                )
            }
            // Max Bet
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PremiumRed.copy(alpha = 0.1f))
                    .border(1.dp, PremiumRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable { onWagerChange(maxCoins.coerceAtMost(5000)) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("MAX", style = MaterialTheme.typography.labelSmall, color = PremiumRed, fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeonButton(
            text = actionText,
            onClick = onAction,
            color = accentColor,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CasinoActionRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun GameSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color.copy(alpha = 0.6f),
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CasinoScaffoldPreview() {
    PetSimulationTheme {
        CasinoScaffold(
            coins = 1500,
            tabs = {
                CasinoTabRow(selected = CasinoGameType.BLACKJACK, onSelected = {})
            },
            content = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("GAME_CONTENT_LOADED", color = CyberYellow, fontWeight = FontWeight.Black)
                }
            },
            logs = {
                Text("TERMINAL_READY", color = Color.White.copy(alpha = 0.4f))
            }
        )
    }
}

@Composable
fun CasinoTabRow(
    selected: CasinoGameType,
    onSelected: (CasinoGameType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CasinoGameType.entries.filter { it != CasinoGameType.ROULETTE }.forEach { type ->
            val isSelected = selected == type
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) CyberYellow.copy(alpha = 0.2f) else Color.Transparent,
                label = "TabBg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) CyberYellow else Color.White.copy(alpha = 0.4f),
                label = "TabText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(bgColor)
                    .clickable { onSelected(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    fontSize = 10.sp
                )
                
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(0.5f)
                            .height(2.dp)
                            .background(CyberYellow)
                    )
                }
            }
        }
    }
}
