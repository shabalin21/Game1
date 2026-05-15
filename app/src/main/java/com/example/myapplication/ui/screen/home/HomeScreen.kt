package com.example.myapplication.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.res.stringResource
import com.example.myapplication.R
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
import com.example.myapplication.domain.model.PsychologyState
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.fx.AtmosphereManager
import com.example.myapplication.ui.fx.ParticleEngine
import com.example.myapplication.ui.render.RenderState
import com.example.myapplication.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: PetViewModel = hiltViewModel(),
    juiceViewModel: com.example.myapplication.ui.animation.JuiceViewModel = hiltViewModel(),
    onNavigateToUpgrades: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCare: () -> Unit = {},
    onNavigateToPlay: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToSocial: () -> Unit = {}
) {
    val renderState by viewModel.renderState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val foodInventory by viewModel.foodInventory.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val atmosphereManager: AtmosphereManager = remember {
        AtmosphereManager(juiceViewModel.juiceManager.particleEngine)
    }

    // Update Atmosphere (Particles) and Music
    LaunchedEffect(renderState, settings.graphics.targetFps) {
        val state = renderState ?: return@LaunchedEffect
        viewModel.musicDirector.update(state)

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
                    atmosphereManager.update(accumulator, state.atmosphere, state.isSleeping)
                    accumulator = 0f
                }
            }
        }
    }

    var showFoodDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SurfaceDark,
                modifier = Modifier.width(300.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(Modifier.height(48.dp))
                    Text(
                        stringResource(R.string.menu_title),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(color = GlassBorder)
                    NavigationDrawerItem(
                        label = { Text("Care") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToCare()
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                    NavigationDrawerItem(
                        label = { Text("Play") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToPlay()
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                    NavigationDrawerItem(
                        label = { Text("Shop") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToShop()
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                    NavigationDrawerItem(
                        label = { Text("Friends") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToSocial()
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) {
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

            renderState?.let { state ->
                val atmosphere = state.atmosphere
                val ambientColor = atmosphereManager.getAmbientColor(atmosphere)

                // Background Glow based on Mood
                val moodColor = when(state.primaryMood) {
                    Mood.ANGRY -> PremiumRed
                    Mood.SAD -> Color(0xFF546E7A)
                    Mood.SLEEPY -> PremiumBlue
                    Mood.EXCITED -> PremiumPink
                    else -> PremiumPurple
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ambientColor)
                        .blur(atmosphere.blurIntensity.dp)
                )

                // Task 3: Nighttime Overlay (extra layer for depth)
                if (state.atmosphere.lightingOverlayAlpha > 0.1f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = state.atmosphere.lightingOverlayAlpha * 0.5f))
                    )
                }

                // Mood Gradient
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(450.dp)
                        .blur(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(moodColor.copy(alpha = 0.12f * state.moodIntensity), Color.Transparent)
                            )
                        )
                )

                HomeScreenContent(
                    state = state,
                    targetFps = settings.graphics.targetFps,
                    canFeed = foodInventory.isNotEmpty(),
                    onFeedClick = { showFoodDialog = true },
                    onPet = { viewModel.petThePet() },
                    onToggleSleep = { viewModel.toggleSleep() },
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToUpgrades = onNavigateToUpgrades,
                    onActivateDevLab = { viewModel.executeCommand("devlab") }
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
}

@Composable
fun HomeScreenContent(
    state: RenderState,
    targetFps: Int = 60,
    canFeed: Boolean,
    onFeedClick: () -> Unit,
    onPet: () -> Unit,
    onToggleSleep: () -> Unit,
    onMenuClick: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToUpgrades: () -> Unit = {},
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
        // HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
                CoinDisplay(coins = state.coins)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateToUpgrades) {
                    Icon(Icons.Default.Star, contentDescription = "Upgrades", tint = PremiumGold)
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // World Info
        Surface(
            color = Color.Black.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "${state.world.timeLabel} ${state.world.weatherIcon} ${state.world.temperatureLabel}",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mood Badge
        MoodBadge(state.primaryMood)

        Spacer(modifier = Modifier.weight(0.5f))

        // CENTERED BUDDY
        Box(
            modifier = Modifier
                .size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            PetSprite(
                emotion = EmotionState(primaryMood = state.primaryMood, intensity = state.moodIntensity),
                psychology = PsychologyState(stress = state.stats.stress, burnout = state.stats.burnout, motivation = state.stats.motivation),
                isSleeping = state.isSleeping,
                appearance = state.appearance,
                equippedItems = state.equippedItems,
                onActivateDevLab = onActivateDevLab,
                onPet = onPet,
                targetFps = targetFps,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Text(
            text = state.activityName.replace("_", " "),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // BOTTOM PANEL
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            showGlow = true
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "BIOMETRICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        AnimatedStatBar(label = "HUNGER", value = state.stats.hunger, color = StatHunger)
                        Spacer(Modifier.height(12.dp))
                        AnimatedStatBar(label = "THIRST", value = state.stats.thirst, color = Color(0xFF81D4FA))
                        Spacer(Modifier.height(12.dp))
                        AnimatedStatBar(label = "MENTAL", value = state.stats.mentalEnergy, color = Color(0xFFCE93D8))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        AnimatedStatBar(label = "ENERGY", value = state.stats.energy, color = StatEnergy)
                        Spacer(Modifier.height(12.dp))
                        AnimatedStatBar(label = "HYGIENE", value = state.stats.hygiene, color = Color(0xFFB2EBF2))
                        Spacer(Modifier.height(12.dp))
                        AnimatedStatBar(label = "STRESS", value = state.stats.stress, color = PremiumRed)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PremiumButton(
                        text = "FEED",
                        onClick = { if (canFeed) { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onFeedClick() } },
                        enabled = canFeed && !state.isSleeping,
                        modifier = Modifier.weight(1f),
                        accentColor = PremiumGold
                    )
                    PremiumButton(
                        text = "PET",
                        onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onPet() },
                        enabled = !state.isSleeping,
                        modifier = Modifier.weight(1f),
                        accentColor = PremiumPink
                    )
                    PremiumButton(
                        text = if (state.isSleeping) "WAKE" else "SLEEP",
                        onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onToggleSleep() },
                        modifier = Modifier.weight(1f),
                        accentColor = if (state.isSleeping) PremiumBlue else PremiumPurple
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MoodBadge(mood: Mood) {
    val color = when(mood) {
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
                text = mood.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
