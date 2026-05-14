package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.*

@Composable
fun PremiumCardBox(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorder,
    backgroundColor: Color = SurfaceCard,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.cornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(Shapes.cornerRadius))
            .then(clickableModifier)
            .padding(Spacing.md),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun PremiumSolidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = PremiumPurple,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.buttonRadius))
            .background(
                if (enabled) Brush.horizontalGradient(
                    listOf(accentColor.copy(alpha = 0.8f), accentColor.copy(alpha = 0.5f))
                ) else SolidColor(Color.Gray.copy(alpha = 0.3f))
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun PremiumGlassOverlay(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(Spacing.xl),
        contentAlignment = Alignment.Center
    ) {
        PremiumCardBox(
            backgroundColor = SurfaceDark.copy(alpha = 0.95f),
            borderColor = GlassBorderStrong,
            content = content
        )
    }
}
