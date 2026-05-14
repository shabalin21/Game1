package com.example.myapplication.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.Mood
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.example.myapplication.ui.animation.InteractionType
import com.example.myapplication.ui.animation.JuiceEvent
import kotlinx.coroutines.launch
import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.domain.model.WorldState
import com.example.myapplication.domain.model.EmotionState
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.fx.AtmosphereManager
import com.example.myapplication.ui.fx.ParticleEngine
import com.example.myapplication.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: PetViewModel = hiltViewModel(),
    juiceViewModel: com.example.myapplication.ui.animation.JuiceViewModel = hiltViewModel(),
    onNavigateToUpgrades: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val cheatManager: com.example.myapplication.domain.admin.CheatManager = 
        remember { (viewModel.interactionSystem.simulationManager.cheatManager) }

    val pet by viewModel.petState.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val btcPrice by viewModel.btcPrice.collectAsState()
    val world by viewModel.worldState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val foodInventory by viewModel.foodInventory.collectAsState()
    val scope = rememberCoroutineScope()

    val atmosphereManager: AtmosphereManager = remember {
        AtmosphereManager(juiceViewModel.juiceManager.particleEngine)
    }

    // Update Atmosphere
    LaunchedEffect(world, settings.graphics.targetFps) {
        var lastTime = withFrameMillis { it }
        val targetFps = settings.graphics.targetFps
        val targetFrameTime = 1f / targetFps
        var accumulator = 0f
        
        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = (currentTime - lastTime) / 1000f
                lastTime = currentTime
                
                accumulator += deltaTime
                if (accumulator >= targetFrameTime) {
                    atmosphereManager.update(accumulator, world)
                    accumulator = 0f
                }
            }
        }
    }

    var showFoodDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    scope.launch {
                        juiceViewModel.juiceManager.triggerEffect(JuiceEvent.Interaction(InteractionType.PET))
                    }
                }
            }
    ) {
        PremiumBackground(accentColor = PremiumPurple) {}

        pet?.let { currentPet ->
            // MOOD-BASED AMBIENT GLOW + WORLD TINT
            val moodColor = when(currentPet.emotionState.primaryMood) {
                Mood.ANGRY -> PremiumRed
                Mood.SAD -> Color(0xFF546E7A)
                Mood.SLEEPY -> PremiumBlue
                Mood.EXCITED -> PremiumPink
                else -> PremiumPurple
            }
            
            val ambientWorldColor = atmosphereManager.getAmbientColor(world)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ambientWorldColor.copy(alpha = 0.1f))
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(450.dp)
                    .blur(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(moodColor.copy(alpha = 0.12f * currentPet.emotionState.intensity), Color.Transparent)
                        )
                    )
            )

            HomeScreenContent(
                pet = currentPet,
                coins = coins,
                btcPrice = btcPrice,
                targetFps = settings.graphics.targetFps,
                canFeed = foodInventory.isNotEmpty(),
                onFeedClick = { showFoodDialog = true },
                onPet = { viewModel.petThePet() },
                onToggleSleep = { viewModel.toggleSleep() },
                onActivateDevLab = { cheatManager.activate() }
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PremiumPurple)
        }

        if (showFoodDialog) {
            FoodSelectionDialog(
                foodItems = foodInventory,
                onDismiss = { showFoodDialog = false },
                onSelect = { itemId -> viewModel.feedPet(itemId) }
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    pet: PetModel,
    coins: Int,
    btcPrice: Float,
    targetFps: Int = 60,
    canFeed: Boolean,
    onFeedClick: () -> Unit,
    onPet: () -> Unit,
    onToggleSleep: () -> Unit,
    onActivateDevLab: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP HUD: Money & BTC
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoinDisplay(coins = coins)
            
            CurrencyCard(
                icon = "₿",
                value = btcPrice.toLong(),
                accentColor = PremiumGold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mood Badge
        MoodBadge(pet.emotionState)

        Spacer(modifier = Modifier.weight(0.5f))

        // CENTERED BUDDY
        Box(
            modifier = Modifier
                .size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            PetSprite(
                emotion = pet.emotionState,
                psychology = pet.psychology,
                isSleeping = pet.isSleeping,
                appearance = pet.appearance,
                modifiers = pet.activeModifiers,
                equippedItems = pet.equippedItems,
                onActivateDevLab = onActivateDevLab,
                onPet = onPet,
                targetFps = targetFps,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Text(
            text = pet.psychology.currentActivity.name.replace("_", " "),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // BOTTOM PANEL: Stats & Actions
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedStatBar(label = "HUNGER", value = pet.stats.hunger, color = StatHunger, modifier = Modifier.weight(1f))
                AnimatedStatBar(label = "ENERGY", value = pet.stats.energy, color = StatEnergy, modifier = Modifier.weight(1f))
                AnimatedStatBar(label = "HAPPINESS", value = pet.stats.happiness, color = StatHappiness, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumButton(
                    text = "Feed",
                    onClick = {
                        if (canFeed) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onFeedClick()
                        }
                    },
                    enabled = canFeed && !pet.isSleeping,
                    modifier = Modifier.weight(1f),
                    accentColor = PremiumGold
                )
                PremiumButton(
                    text = "Pet",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPet()
                    },
                    enabled = !pet.isSleeping,
                    modifier = Modifier.weight(1f),
                    accentColor = PremiumPink
                )
                PremiumButton(
                    text = if (pet.isSleeping) "Wake" else "Sleep",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleSleep()
                    },
                    modifier = Modifier.weight(1f),
                    accentColor = if (pet.isSleeping) PremiumBlue else PremiumPurple
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MoodBadge(emotion: EmotionState) {
    val color = when(emotion.primaryMood) {
        Mood.ANGRY -> PremiumRed
        Mood.SAD -> Color(0xFF546E7A)
        Mood.SLEEPY -> PremiumBlue
        Mood.EXCITED -> PremiumPink
        else -> PremiumPurple
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = emotion.primaryMood.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
