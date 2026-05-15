package com.example.myapplication.ui.screen.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.ItemRegistry
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*

@Composable
fun BlackMarketScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val coins by viewModel.coins.collectAsState()
    val blackMarketItems by viewModel.blackMarketSelection.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        CyberBackground(accentColor = PremiumPink)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "UNDERG_NET",
                subtitle = "ILLEGAL_ENHANCEMENTS",
                accentColor = PremiumPink,
                trailingContent = { 
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )

            if (blackMarketItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "CONNECTION_LOST: MARKET_OFFLINE",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PremiumPink.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(blackMarketItems.mapNotNull { ItemRegistry.getItem(it) }, key = { it.id }) { itemModel ->
                        ShopItemCard(
                            item = itemModel,
                            canAfford = coins >= itemModel.price,
                            isOwned = false,
                            quantity = 0,
                            onClick = { viewModel.buyItem(itemModel) }
                        )
                    }
                }
            }
        }
    }
}


