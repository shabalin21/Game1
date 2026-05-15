package com.example.myapplication.ui.screen.system

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.admin.CheatManager
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*
import java.util.Locale

@Composable
fun CheatMenu(
    viewModel: CheatViewModel,
    onBack: () -> Unit
) {
    var isUnlocked by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        CyberBackground(accentColor = PremiumRed)

        if (!isUnlocked) {
            CheatLockScreen(
                onUnlocked = { isUnlocked = true },
                onBack = onBack
            )
        } else {
            AdminPanel(
                viewModel = viewModel,
                onBack = onBack
            )
        }
    }
}

@Composable
fun CheatLockScreen(onUnlocked: () -> Unit, onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            repeat(6) { i ->
                shakeOffset.animateTo(if (i % 2 == 0) 10f else -10f, animationSpec = tween(50))
            }
            shakeOffset.animateTo(0f)
            isError = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SYSTEM ACCESS",
            style = MaterialTheme.typography.headlineMedium,
            color = PremiumRed,
            fontWeight = FontWeight.Black
        )
        Text(
            "CREDENTIALS_REQUIRED",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Password Slots
        Row(
            modifier = Modifier.offset(x = shakeOffset.value.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) { index ->
                val char = password.getOrNull(index)
                val isCurrent = index == password.length
                
                Box(
                    modifier = Modifier
                        .size(40.dp, 56.dp)
                        .border(
                            1.dp,
                            if (isError) PremiumRed else if (isCurrent) PremiumRed.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .background(if (isCurrent) PremiumRed.copy(alpha = 0.05f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (char != null) {
                        Box(modifier = Modifier.size(8.dp).background(Color.White, RoundedCornerShape(2.dp)))
                    } else if (isCurrent) {
                        BlinkingCursor()
                    }
                }
            }
        }

        if (isError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("ACCESS_DENIED", color = PremiumRed, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Numeric Keypad
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
            keys.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { key ->
                        CyberButton(
                            text = key,
                            onClick = {
                                when (key) {
                                    "C" -> password = ""
                                    "OK" -> {
                                        if (password == "2121") onUnlocked()
                                        else {
                                            isError = true
                                            password = ""
                                        }
                                    }
                                    else -> if (password.length < 4) password += key
                                }
                            },
                            color = if (key == "OK") PremiumGreen else if (key == "C") PremiumRed else Color.DarkGray,
                            modifier = Modifier.size(70.dp, 50.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(onClick = onBack) {
            Text("ABORT_SEQUENCE", color = Color.White.copy(alpha = 0.3f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun AdminPanel(viewModel: CheatViewModel, onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("STATS") }
    val tabs = listOf("STATS", "PROGRESSION", "ECONOMY", "ITEMS", "SOCIAL", "WORK", "TIME", "WEATHER", "CASINO", "SECRET", "NOTES")
    val cheatManager = viewModel.cheatManager

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        ScreenHeader(
            title = "ADMIN PANEL",
            subtitle = "RESTRICTED SYSTEM ACCESS",
            accentColor = PremiumRed,
            trailingContent = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        )

        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(selectedTab),
            containerColor = Color.Transparent,
            contentColor = PremiumRed,
            edgePadding = 0.dp,
            divider = {}
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    text = { 
                        Text(
                            text = tab, 
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) PremiumRed else Color.White.copy(alpha = 0.4f),
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                        ) 
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedTab) {
                "STATS" -> PlayerCheats(cheatManager)
                "PROGRESSION" -> ProgressionCheats(cheatManager)
                "ECONOMY" -> EconomyCheats(cheatManager)
                "ITEMS" -> ItemGiver(cheatManager)
                "SOCIAL" -> SocialCheats(cheatManager)
                "WORK" -> WorkCheats(cheatManager)
                "TIME" -> TimeCheats(cheatManager)
                "WEATHER" -> WeatherCheats(cheatManager)
                "CASINO" -> CasinoCheats(cheatManager)
                "SECRET" -> SecretRoomContents(cheatManager)
                "NOTES" -> AdminNotesRoom(viewModel)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AdminNotesRoom(viewModel: CheatViewModel) {
    val notes by viewModel.adminNotes.collectAsState()
    
    CheatSection("DEVELOPER_MEMO") {
        Column {
            Text("PRIVATE NOTES - AUTOSAVED", style = MaterialTheme.typography.labelSmall, color = PremiumGreen.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp, max = 500.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = notes,
                    onValueChange = { viewModel.saveNotes(it) },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        color = PremiumGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(PremiumGreen),
                    decorationBox = { innerTextField ->
                        if (notes.isEmpty()) {
                            Text("Initialize recording...", color = PremiumGreen.copy(alpha = 0.2f), fontFamily = FontFamily.Monospace)
                        }
                        innerTextField()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("DATA_PERSISTENCE: ENCRYPTED", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun BlinkingCursor() {
    val alpha by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = ""
    )
    Box(modifier = Modifier.size(2.dp, 24.dp).background(PremiumRed.copy(alpha = alpha)))
}

@Composable
fun SecretRoomContents(cheatManager: CheatManager) {
    CheatSection("ADVANCED_CONTROLS") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CyberButton(text = "FULL_SYSTEM_RESET", onClick = { cheatManager.refillAllStats() }, color = PremiumRed, modifier = Modifier.fillMaxWidth())
            
            val godMode by cheatManager.godModeEnabled.collectAsState()
            CheatToggle("GOD_MODE (NO_DECAY)", godMode) { cheatManager.godModeEnabled.value = it }
        }
    }
}

@Composable
fun PlayerCheats(cheatManager: CheatManager) {
    CheatSection("BIOMETRIC_OVERRIDE") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val stats = listOf("Hunger", "Energy", "Happiness", "Health", "Stress", "Social")
            stats.forEach { stat ->
                StatCheatRow(stat, onSet = { cheatManager.setStat(stat, it) })
            }
        }
    }
}

@Composable
fun StatCheatRow(label: String, onSet: (Float) -> Unit) {
    var value by remember { mutableFloatStateOf(100f) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.width(80.dp))
        Slider(
            value = value,
            onValueChange = { value = it },
            valueRange = 0f..100f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = PremiumRed, activeTrackColor = PremiumRed, inactiveTrackColor = Color.White.copy(alpha = 0.1f))
        )
        TextButton(onClick = { onSet(value) }) {
            Text("SET", style = MaterialTheme.typography.labelSmall, color = PremiumRed, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun EconomyCheats(cheatManager: CheatManager) {
    CheatSection("ECONOMY") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CyberButton(text = "+1K CR", onClick = { cheatManager.addMoney(1000) }, color = PremiumRed, modifier = Modifier.weight(1f))
                CyberButton(text = "+10K CR", onClick = { cheatManager.addMoney(10000) }, color = PremiumRed, modifier = Modifier.weight(1f))
            }
            
            CyberButton(text = "PURGE_INVENTORY", onClick = { cheatManager.clearInventory() }, color = PremiumRed, modifier = Modifier.fillMaxWidth())

            val infinite by cheatManager.infiniteMoney.collectAsState()
            CheatToggle("INFINITE CREDITS", infinite) { cheatManager.infiniteMoney.value = it }
        }
    }
}

@Composable
fun TimeCheats(cheatManager: CheatManager) {
    CheatSection("TEMPORAL_CONTROL") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val paused by cheatManager.simulationPaused.collectAsState()
            CheatToggle("FREEZE_TIME", paused) { cheatManager.simulationPaused.value = it }
            
            var dilation by remember { mutableFloatStateOf(1.0f) }
            Text("DILATION: ${String.format(Locale.US, "%.1f", dilation)}x", style = MaterialTheme.typography.labelSmall, color = Color.White)
            Slider(
                value = dilation,
                onValueChange = { 
                    dilation = it
                    cheatManager.timeDilation.value = it
                },
                valueRange = 0.1f..10.0f,
                colors = SliderDefaults.colors(thumbColor = PremiumRed, activeTrackColor = PremiumRed, inactiveTrackColor = Color.White.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
fun ProgressionCheats(cheatManager: CheatManager) {
    CheatSection("NEURAL_LEVELING") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCheatRow("LEVEL", onSet = { cheatManager.setLevel(it.toInt()) })
            StatCheatRow("ADD_XP", onSet = { cheatManager.addXp(it.toLong()) })
            
            CyberButton(text = "RESET_PROGRESSION", onClick = { /* reset logic */ }, color = PremiumRed, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun SocialCheats(cheatManager: CheatManager) {
    CheatSection("NETWORK_INFLUENCE") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCheatRow("FOLLOWERS", onSet = { cheatManager.setFollowers(it.toInt()) })
            StatCheatRow("PRESTIGE", onSet = { cheatManager.setSocialPrestige(it.toInt()) })
            
            CyberButton(text = "FORCE_NPC_POST", onClick = { /* force logic */ }, color = PremiumBlue, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun WorkCheats(cheatManager: CheatManager) {
    CheatSection("CAREER_OVERRIDE") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CyberButton(text = "MAX_JOB_EXP", onClick = { /* max exp logic */ }, color = PremiumGreen, modifier = Modifier.fillMaxWidth())
            CyberButton(text = "RESET_CAREER", onClick = { /* reset logic */ }, color = PremiumRed, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun WeatherCheats(cheatManager: CheatManager) {
    CheatSection("ATMOSPHERIC_MANIPULATION") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Weather.entries.forEach { weather ->
                CyberButton(
                    text = weather.name,
                    onClick = { cheatManager.setWeather(weather) },
                    color = when (weather) {
                        Weather.SUNNY -> PremiumGold
                        Weather.RAINY -> PremiumBlue
                        Weather.CLOUDY -> Color.Gray
                        Weather.STORMY -> PremiumPurple
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CasinoCheats(cheatManager: CheatManager) {
    CheatSection("CASINO_OVERRIDE") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val bypassFee by cheatManager.bypassCasinoFee.collectAsState()
            CheatToggle("BYPASS_FEE", bypassFee) { cheatManager.bypassCasinoFee.value = it }
            
            val guaranteedWin by cheatManager.guaranteedCasinoWin.collectAsState()
            CheatToggle("GUARANTEED_WIN", guaranteedWin) { cheatManager.guaranteedCasinoWin.value = it }
        }
    }
}

@Composable
fun ItemGiver(cheatManager: CheatManager) {
    CheatSection("MATERIEL_INJECTION") {
        Text("MANUAL_ID_INJECTION", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("ENERGY_DRINK", style = MaterialTheme.typography.labelSmall, color = Color.White)
            CyberButton(text = "SPAWN", onClick = { cheatManager.giveItem("energy_drink") }, color = PremiumRed)
        }
    }
}

@Composable
fun CheatSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = PremiumRed.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        CyberCard(accentColor = PremiumRed) {
            Box(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun CheatToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White)
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange, 
            colors = SwitchDefaults.colors(
                checkedThumbColor = PremiumRed, 
                checkedTrackColor = PremiumRed.copy(alpha = 0.2f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.White.copy(alpha = 0.05f)
            )
        )
    }
}

