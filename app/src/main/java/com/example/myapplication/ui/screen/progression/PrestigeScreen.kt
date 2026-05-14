package com.example.myapplication.ui.screen.progression

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.progression.PrestigeManager
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PrestigeScreen(
    viewModel: PrestigeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val prestigeManager = viewModel.prestigeManager
    val scope = rememberCoroutineScope()
    var canPrestige by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        canPrestige = prestigeManager.canPrestige()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CyberBackground(accentColor = CyberPurple)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "REBIRTH_PROTOCOL",
                subtitle = "INFINITE_EVOLUTION",
                accentColor = CyberPurple,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                "READY TO ASCEND?",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Reset your wealth and status to gain permanent neural multipliers. Your name and Mythic possessions will remain.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            CyberCard(accentColor = if (canPrestige) CyberPurple else Color.White.copy(alpha = 0.1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PERMANENT BONUS", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                    Text("+50% INCOME", style = MaterialTheme.typography.titleLarge, color = CyberPurple, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            CyberButton(
                text = "EXECUTE REBIRTH",
                onClick = { 
                    scope.launch {
                        if (prestigeManager.performRebirth()) {
                            onBack()
                        }
                    }
                },
                enabled = canPrestige,
                color = CyberPurple,
                modifier = Modifier.fillMaxWidth()
            )

            if (!canPrestige) {
                Text(
                    "REQUIREMENT: LVL 100 OR 10M CR",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
