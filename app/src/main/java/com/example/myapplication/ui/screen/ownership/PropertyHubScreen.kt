package com.example.myapplication.ui.screen.ownership

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
fun PropertyHubScreen(
    onNavigateToOwnership: () -> Unit,
    onNavigateToUpgrades: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CyberBackground(accentColor = PremiumBlue)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "PROPERTY_MANAGEMENT",
                subtitle = "LIFESTYLE_ASSETS",
                accentColor = PremiumBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            HubSectionHeader("REAL_ESTATE_&_TRANSPORT")
            HubCategoryCard(
                title = "OWNERSHIP_GALLERY",
                subtitle = "Homes, Vehicles & Pets",
                icon = Icons.Default.Home,
                color = PremiumPurple,
                onClick = onNavigateToOwnership
            )

            Spacer(modifier = Modifier.height(24.dp))

            HubSectionHeader("SYSTEM_ENHANCEMENTS")
            HubCategoryCard(
                title = "UPGRADES_TERMINAL",
                subtitle = "Permanent Boosts & Efficiency",
                icon = Icons.Default.Settings,
                color = PremiumBlue,
                onClick = onNavigateToUpgrades
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

