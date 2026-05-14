package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import java.text.NumberFormat
import java.util.*

@Composable
fun CurrencyCard(
    icon: String,
    value: Long,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.White
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val formattedValue = formatter.format(value)

    Row(
        modifier = modifier
            .height(36.dp)
            .widthIn(min = 100.dp) // Ensure minimum width for consistency
            .clip(RoundedCornerShape(Shapes.buttonRadius))
            .background(GlassBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(Shapes.buttonRadius))
            .padding(horizontal = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = icon, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = formattedValue,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun CoinDisplay(coins: Int, modifier: Modifier = Modifier) {
    CurrencyCard(
        icon = "💰",
        value = coins.toLong(),
        modifier = modifier
    )
}
