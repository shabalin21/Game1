package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.casino.model.*
import com.example.myapplication.ui.component.NeonButton
import com.example.myapplication.ui.theme.*

@Composable
fun BlackjackView(
    state: BlackjackState,
    coins: Int,
    onHit: () -> Unit,
    onStand: () -> Unit,
    onBet: (Int) -> Unit
) {
    var selectedWager by remember { mutableIntStateOf(100) }

    CasinoGameCard(accentColor = NeonBlue) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val cardWidth = (maxWidth / 5).coerceIn(40.dp, 60.dp)
                val cardHeight = cardWidth * 1.4f
                
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state) {
                        is BlackjackState.Idle, is BlackjackState.Betting -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                GameSectionHeader("Select Bet", color = NeonBlue)
                                Spacer(modifier = Modifier.height(16.dp))
                                BettingControls(
                                    selectedWager = selectedWager,
                                    onWagerChange = { selectedWager = it },
                                    onAction = { onBet(selectedWager) },
                                    actionText = "DEAL",
                                    enabled = coins >= selectedWager,
                                    accentColor = NeonBlue,
                                    maxCoins = coins,
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                )
                            }
                        }
                        else -> {
                            val playerHand = when(state) {
                                is BlackjackState.Dealing -> state.playerHand
                                is BlackjackState.PlayerTurn -> state.playerHand
                                is BlackjackState.DealerTurn -> state.playerHand
                                is BlackjackState.Result -> state.playerHand
                                else -> emptyList()
                            }
                            val dealerHand = when(state) {
                                is BlackjackState.Dealing -> state.dealerHand
                                is BlackjackState.PlayerTurn -> state.dealerHand
                                is BlackjackState.DealerTurn -> state.dealerHand
                                is BlackjackState.Result -> state.dealerHand
                                else -> emptyList()
                            }

                            // DEALER AREA
                            HandDisplay(
                                title = "Dealer", 
                                cards = dealerHand, 
                                isDealer = true, 
                                hideFirst = state is BlackjackState.PlayerTurn || state is BlackjackState.Dealing,
                                score = if (state is BlackjackState.PlayerTurn || state is BlackjackState.Dealing) null else calculateScore(dealerHand),
                                cardWidth = cardWidth,
                                cardHeight = cardHeight
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            // PLAYER AREA
                            HandDisplay(
                                title = "Player", 
                                cards = playerHand,
                                score = calculateScore(playerHand),
                                cardWidth = cardWidth,
                                cardHeight = cardHeight
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // RESULT OR ACTIONS
                            Box(modifier = Modifier.height(100.dp), contentAlignment = Alignment.Center) {
                                AnimatedContent(
                                    targetState = state,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "BlackjackActions"
                                ) { currentState ->
                                    when (currentState) {
                                        is BlackjackState.PlayerTurn -> {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                NeonButton(
                                                    text = "Hit", 
                                                    onClick = onHit, 
                                                    color = NeonCyan,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                NeonButton(
                                                    text = "Stand", 
                                                    onClick = onStand, 
                                                    color = NeonPink,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                        is BlackjackState.Result -> {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                BlackjackResultBanner(outcome = currentState.outcome, payout = currentState.payout)
                                                Spacer(modifier = Modifier.height(12.dp))
                                                NeonButton(
                                                    text = "New Hand", 
                                                    onClick = { onBet(currentState.bet) }, 
                                                    color = NeonBlue,
                                                    modifier = Modifier.width(160.dp)
                                                )
                                            }
                                        }
                                        else -> {
                                            Text(
                                                "Dealing...", 
                                                color = NeonBlue, 
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HandDisplay(
    title: String, 
    cards: List<Card>, 
    isDealer: Boolean = false, 
    hideFirst: Boolean = false,
    score: Int? = null,
    cardWidth: androidx.compose.ui.unit.Dp,
    cardHeight: androidx.compose.ui.unit.Dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Black)
            if (score != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "[$score]", 
                    color = if (score > 21) NeonRed else NeonCyan, 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(cards) { index, card ->
                BlackjackCardView(
                    card = card, 
                    hidden = isDealer && hideFirst && index == 0,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = cardWidth, height = cardHeight)
                )
            }
        }
    }
}

@Composable
fun BlackjackCardView(card: Card, hidden: Boolean, modifier: Modifier = Modifier) {
    val color = if (hidden) SurfaceDark else Color.White
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .border(
                width = 1.dp, 
                color = if (hidden) NeonBlue.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.1f), 
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!hidden) {
            val textColor = if (card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS) Color(0xFFD32F2F) else Color(0xFF212121)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(card.rank.symbol, color = textColor, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text(card.suit.symbol, color = textColor, fontSize = 20.sp)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.verticalGradient(listOf(SurfaceDark, Color.Black))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "?", 
                    color = NeonBlue, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun BlackjackResultBanner(outcome: BlackjackOutcome, payout: Int) {
    val (text, color) = when(outcome) {
        BlackjackOutcome.WIN -> "Winner!" to NeonGreen
        BlackjackOutcome.BLACKJACK -> "Blackjack!" to NeonYellow
        BlackjackOutcome.LOSS -> "House Wins" to NeonRed
        BlackjackOutcome.BUST -> "Bust!" to Color.Red
        BlackjackOutcome.PUSH -> "Push" to Color.Gray
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = text, 
            color = color, 
            fontWeight = FontWeight.Black, 
            fontSize = 20.sp,
            letterSpacing = 1.sp
        )
        if (payout > 0) {
            Text(
                "+$payout CR", 
                color = NeonCyan, 
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

private fun calculateScore(cards: List<Card>): Int {
    var score = 0
    var aces = 0
    for (card in cards) {
        score += when (card.rank) {
            Rank.ACE -> { aces++; 11 }
            Rank.JACK, Rank.QUEEN, Rank.KING -> 10
            else -> card.rank.value
        }
    }
    while (score > 21 && aces > 0) {
        score -= 10
        aces--
    }
    return score
}
