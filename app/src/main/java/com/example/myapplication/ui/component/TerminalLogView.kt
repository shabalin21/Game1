package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLog
import com.example.myapplication.ui.theme.*

@Composable
fun TerminalLogView(
    logs: List<TerminalLog>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(8.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(logs) { log ->
                Row(modifier = Modifier.padding(vertical = 1.dp)) {
                    Text(
                        text = "[${log.timestamp}] ",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "[${log.category}] ",
                        color = getCategoryColor(log.category),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = log.message,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

private fun getCategoryColor(category: LogCategory): Color = when (category) {
    LogCategory.ECONOMY -> NeonGreen
    LogCategory.CASINO -> NeonPink
    LogCategory.NEURAL -> NeonBlue
    LogCategory.EMOTION -> NeonPurple
    LogCategory.WARNING -> NeonOrange
    LogCategory.CRITICAL -> Color.Red
    LogCategory.SYSTEM -> Color.Cyan
    LogCategory.MARKET -> NeonCyan
}
