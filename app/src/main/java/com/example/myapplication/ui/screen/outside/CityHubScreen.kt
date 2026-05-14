package com.example.myapplication.ui.screen.outside

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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CityHubScreen(
    onNavigateToWork: () -> Unit,
    onNavigateToCasino: () -> Unit,
    onNavigateToBlackMarket: () -> Unit,
    onNavigateToInvestments: () -> Unit,
    onNavigateToMinigames: () -> Unit,
    onNavigateToStore: () -> Unit,
    onNavigateToProperty: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        PremiumBackground(accentColor = PremiumBlue) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "Metropolis",
                subtitle = "Central District",
                accentColor = PremiumBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            // INFRASTRUCTURE
            HubSectionHeader("INFRASTRUCTURE")
            HubCategoryCard(
                title = "Work",
                subtitle = "Career & Employment",
                icon = Icons.Default.Build,
                color = PremiumGreen,
                onClick = onNavigateToWork
            )

            HubCategoryCard(
                title = "Property",
                subtitle = "Real Estate & Ownership",
                icon = Icons.Default.Home,
                color = PremiumPurple,
                onClick = onNavigateToProperty
            )

            HubCategoryCard(
                title = "Store",
                subtitle = "Essentials & Gear",
                icon = Icons.Default.ShoppingCart,
                color = PremiumGold,
                onClick = onNavigateToStore
            )

            HubCategoryCard(
                title = "Stock Market",
                subtitle = "Global Investments",
                icon = Icons.Default.Info, 
                color = NeonCyan,
                onClick = onNavigateToInvestments
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ENTERTAINMENT
            HubSectionHeader("ENTERTAINMENT")
            HubCategoryCard(
                title = "Casino",
                subtitle = "Premium Gambling",
                icon = Icons.Default.Star,
                color = PremiumPink,
                onClick = onNavigateToCasino
            )

            HubCategoryCard(
                title = "Games",
                subtitle = "Recreational Center",
                icon = Icons.Default.PlayArrow,
                color = PremiumPurple,
                onClick = onNavigateToMinigames
            )

            Spacer(modifier = Modifier.height(24.dp))

            // RESTRICTED
            HubSectionHeader("RESTRICTED")
            HubCategoryCard(
                title = "Black Market",
                subtitle = "Exclusive Enhancements",
                icon = Icons.Default.Lock,
                color = PremiumRed,
                onClick = onNavigateToBlackMarket
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HubSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White.copy(alpha = 0.4f),
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Composable
fun HubCategoryCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        borderColor = color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Normal
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}
