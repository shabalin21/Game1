package com.example.myapplication.ui.screen.casino

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.casino.model.*
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.screen.casino.components.*
import com.example.myapplication.ui.screen.casino.model.CasinoRules
import com.example.myapplication.ui.screen.casino.model.GameRule
import com.example.myapplication.ui.theme.*

@Composable
fun CasinoScreen(
    viewModel: CasinoViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val coins by viewModel.coins.collectAsState()
    val pet by viewModel.petState.collectAsState()
    val logs by viewModel.logManager.logs.collectAsState()
    
    val blackjackState by viewModel.blackjackState.collectAsState()
    val slotsState by viewModel.slotsState.collectAsState()
    val coinFlipState by viewModel.coinFlipState.collectAsState()
    val crashState by viewModel.crashState.collectAsState()
    val plinkoState by viewModel.plinkoState.collectAsState()

    val entryAuthorized by viewModel.entryAuthorized.collectAsState()
    var activeRules by remember { mutableStateOf<GameRule?>(null) }
    var selectedGame by remember { mutableStateOf(CasinoGameType.BLACKJACK) }

    Box(modifier = Modifier.fillMaxSize()) {
        NeonBackground(accentColor = NeonYellow)

        val isBanned = pet?.casinoSession?.isBanned == true

        if (isBanned) {
            CasinoSecurityScreen(
                onPayReEntry = { viewModel.payReEntry() },
                onLeave = onBack,
                reEntryFee = pet?.casinoSession?.reEntryFee ?: 500
            )
        } else if (!entryAuthorized) {
            CasinoEntryGate(
                canAfford = coins >= 1000,
                onEnter = { viewModel.tryEnter() }
            )
        } else {
            CasinoScaffold(
                coins = coins,
                topBarActions = {
                    IconButton(onClick = {
                        activeRules = when(selectedGame) {
                            CasinoGameType.BLACKJACK -> CasinoRules.blackjack
                            CasinoGameType.SLOTS -> CasinoRules.slots
                            CasinoGameType.COINFLIP -> CasinoRules.coinflip
                            CasinoGameType.CRASH -> CasinoRules.crash
                            CasinoGameType.PLINKO -> CasinoRules.plinko
                            else -> CasinoRules.blackjack
                        }
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "Rules", tint = NeonBlue)
                    }
                },
                tabs = {
                    CasinoTabRow(
                        selected = selectedGame,
                        onSelected = { selectedGame = it }
                    )
                },
                content = {
                    AnimatedContent(
                        targetState = selectedGame,
                        transitionSpec = {
                            fadeIn(tween(300)) + slideInHorizontally { it / 8 } togetherWith 
                            fadeOut(tween(200)) + slideOutHorizontally { -it / 8 }
                        },
                        label = "CasinoGameAnim",
                        modifier = Modifier.fillMaxSize()
                    ) { game ->
                        when (game) {
                            CasinoGameType.BLACKJACK -> BlackjackView(
                                state = blackjackState,
                                coins = coins,
                                onHit = { viewModel.blackjackHit() },
                                onStand = { viewModel.blackjackStand() },
                                onBet = { viewModel.startBlackjack(it) }
                            )
                            CasinoGameType.SLOTS -> SlotsView(
                                state = slotsState,
                                coins = coins,
                                onSpin = { viewModel.playSlots(it) }
                            )
                            CasinoGameType.COINFLIP -> CoinFlipView(
                                state = coinFlipState,
                                coins = coins,
                                onFlip = { bet, side -> viewModel.playCoinFlip(bet, side) }
                            )
                            CasinoGameType.CRASH -> CrashView(
                                state = crashState,
                                coins = coins,
                                onStart = { viewModel.startCrash(it) },
                                onCashOut = { viewModel.cashOutCrash() }
                            )
                            CasinoGameType.PLINKO -> PlinkoView(
                                state = plinkoState,
                                coins = coins,
                                onDrop = { viewModel.dropPlinkoBall(it) },
                                onRiskChange = { viewModel.setPlinkoRisk(it) }
                            )
                            else -> {}
                        }
                    }
                },
                logs = {
                    Column {
                        GameSectionHeader("Recent Wins", color = NeonYellow)
                        TerminalLogView(
                            logs = logs,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(SurfaceDark.copy(alpha = 0.5f))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), MaterialTheme.shapes.small)
                        )
                    }
                }
            )
        }

        activeRules?.let {
            RulesOverlay(rule = it, onDismiss = { activeRules = null })
        }
    }
}

@Composable
fun CasinoEntryGate(
    canAfford: Boolean,
    onEnter: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        NeonCard(accentColor = if (canAfford) NeonBlue else NeonRed, modifier = Modifier.padding(32.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Warning, 
                    contentDescription = null, 
                    tint = if (canAfford) NeonBlue else NeonRed,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (canAfford) "Welcome back" else "Low Balance",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (canAfford) NeonBlue else NeonRed,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (canAfford) "Entrance fee is 1000 CR. Would you like to enter?" else "You need at least 1000 CR to enter the lounge.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                if (canAfford) {
                    NeonButton(
                        text = "Enter Lounge",
                        onClick = onEnter,
                        color = NeonBlue
                    )
                } else {
                    Text(
                        "Access Denied",
                        color = NeonRed,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
