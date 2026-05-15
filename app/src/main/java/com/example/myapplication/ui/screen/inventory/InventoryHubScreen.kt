package com.example.myapplication.ui.screen.inventory

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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun InventoryHubScreen(
    onNavigateToCargo: () -> Unit,
    onNavigateToAppearance: () -> Unit
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
                title = "Inventory",
                subtitle = "Storage & Equipment",
                accentColor = PremiumBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            HubSectionHeader("PERSONALIZATION")
            HubCategoryCard(
                title = "Wardrobe",
                subtitle = "Customize Appearance & Outfits",
                icon = Icons.Default.Face,
                color = PremiumPink,
                onClick = onNavigateToAppearance
            )

            Spacer(modifier = Modifier.height(24.dp))

            HubSectionHeader("LOGISTICS")
            HubCategoryCard(
                title = "Inventory",
                subtitle = "Items, consumables & supplies",
                icon = Icons.AutoMirrored.Filled.List,
                color = PremiumBlue,
                onClick = onNavigateToCargo
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

