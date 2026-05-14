package com.example.myapplication.ui.screen.social

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
fun SocialHubScreen(
    onNavigateToFeed: () -> Unit,
    onNavigateToMissions: () -> Unit
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
                title = "SOCIAL_NETWORK",
                subtitle = "NEURAL_LINK_FEED",
                accentColor = CyberBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            HubSectionHeader("COMMUNITY")
            HubCategoryCard(
                title = "NEURAL_FEED",
                subtitle = "Followers & Social Flexing",
                icon = Icons.Default.Share,
                color = NeonCyan,
                onClick = onNavigateToFeed
            )

            Spacer(modifier = Modifier.height(24.dp))

            HubSectionHeader("OBJECTIVES")
            HubCategoryCard(
                title = "MISSION_TERMINAL",
                subtitle = "Daily & Weekly Challenges",
                icon = Icons.Default.Info,
                color = CyberGreen,
                onClick = onNavigateToMissions
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
