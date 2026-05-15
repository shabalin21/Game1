package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.screen.casino.model.GameRule
import com.example.myapplication.ui.theme.*

@Composable
fun RulesOverlay(
    rule: GameRule,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BackgroundDark.copy(alpha = 0.95f))
                .border(1.dp, PremiumBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = rule.title,
                    color = PremiumBlue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = rule.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "PAYOUT_PROTOCOL",
                    color = PremiumCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                rule.payouts.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text(value, color = PremiumGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PremiumBlue.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PremiumBlue)
                ) {
                    Text("CLOSE_LINK", color = PremiumBlue, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

