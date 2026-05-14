package com.example.myapplication.ui.screen.casino

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*

@Composable
fun CasinoSecurityScreen(
    onPayReEntry: () -> Unit,
    onLeave: () -> Unit,
    reEntryFee: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        CyberCard(
            modifier = Modifier.padding(24.dp),
            accentColor = CyberRed
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SECURITY INTERCEPTION",
                    style = MaterialTheme.typography.headlineSmall,
                    color = CyberRed,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "“Our management has noticed your unusual winning streak. For the safety of the house, your active session has been suspended.”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "A VIP clearance fee is required for immediate re-entry.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                CyberButton(
                    text = "PAY $reEntryFee CR",
                    onClick = onPayReEntry,
                    color = CyberRed,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(onClick = onLeave) {
                    Text("Leave Quietly", color = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}
