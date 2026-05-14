package com.example.myapplication.ui.screen.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*
import com.example.myapplication.ui.screen.outside.HubCategoryCard
import com.example.myapplication.ui.screen.outside.HubSectionHeader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ProfileHubScreen(
    onNavigateToStats: () -> Unit,
    onNavigateToPrestige: () -> Unit,
    onNavigateToDebug: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CyberBackground(accentColor = CyberBlue)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "USER_PROFILE",
                subtitle = "NEURAL_SYNCHRONIZATION",
                accentColor = CyberBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            HubSectionHeader("LIFETIME_DATA")
            HubCategoryCard(
                title = "STATISTICS_CORE",
                subtitle = "Achievements & Analytics",
                icon = Icons.Default.Info,
                color = CyberBlue,
                onClick = onNavigateToStats
            )

            Spacer(modifier = Modifier.height(24.dp))

            HubSectionHeader("EVOLUTION")
            HubCategoryCard(
                title = "PRESTIGE_PROTOCOL",
                subtitle = "Rebirth & Ascension",
                icon = Icons.Default.KeyboardArrowUp,
                color = CyberPurple,
                onClick = onNavigateToPrestige
            )

            Spacer(modifier = Modifier.height(24.dp))

            HubSectionHeader("SYSTEM")
            HubCategoryCard(
                title = "Settings",
                subtitle = "Preferences & Control",
                icon = Icons.Default.Settings,
                color = Color.Gray,
                onClick = onNavigateToSettings
            )

            HubCategoryCard(
                title = "ROOT_ACCESS",
                subtitle = "Developer Calibration",
                icon = Icons.Default.Build,
                color = CyberRed,
                onClick = onNavigateToDebug
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
