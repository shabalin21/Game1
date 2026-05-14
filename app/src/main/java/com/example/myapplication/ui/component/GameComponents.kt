package com.example.myapplication.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceCard,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.cardRadius))
            .background(backgroundColor)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(Shapes.cardRadius)
            )
            .padding(Spacing.md)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorder,
    showGlow: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.cardRadius))
            .background(GlassBackground)
            .border(
                1.dp,
                if (showGlow) borderColor.copy(alpha = 0.2f) else GlassBorder,
                RoundedCornerShape(Shapes.cardRadius)
            )
            .padding(Spacing.md)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun AnimatedStatBar(
    label: String,
    value: Float,
    max: Float = 100f,
    color: Color,
    modifier: Modifier = Modifier
) {
    val normalizedValue = (value / max).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = normalizedValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "StatProgress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = "${value.roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xs))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.6f), color)
                        )
                    )
            )
        }
    }
}

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = SurfaceDark,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(Shapes.buttonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color.White.copy(alpha = 0.03f),
            disabledContentColor = Color.White.copy(alpha = 0.2f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(horizontal = Spacing.lg)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = PremiumPurple,
    enabled: Boolean = true
) {
    GameButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        containerColor = accentColor.copy(alpha = 0.1f),
        contentColor = accentColor
    )
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    accentColor: Color = PrimaryColor,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
        if (trailingContent != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                trailingContent()
            }
        }
    }
}

@Composable
fun PremiumBackground(
    accentColor: Color = PrimaryColor,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Subtle gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun PremiumBadge(
    text: String,
    accentColor: Color = PrimaryColor,
    modifier: Modifier = Modifier
) {
    Surface(
        color = accentColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// LEGACY ALIASES (Refactor these away)
@Composable
fun CyberBackground(accentColor: Color, content: @Composable BoxScope.() -> Unit = {}) {
    PremiumBackground(accentColor, content)
}

@Composable
fun NeonBackground(accentColor: Color, content: @Composable BoxScope.() -> Unit = {}) {
    PremiumBackground(accentColor, content)
}

@Composable
fun CyberCard(modifier: Modifier = Modifier, accentColor: Color = PrimaryColor, showGlow: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    GlassCard(modifier, accentColor, showGlow, content)
}

@Composable
fun NeonCard(modifier: Modifier = Modifier, accentColor: Color = PrimaryColor, showGlow: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    GlassCard(modifier, accentColor, showGlow, content)
}

@Composable
fun NeonButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = PrimaryColor, enabled: Boolean = true) {
    PremiumButton(text, onClick, modifier, color, enabled)
}

@Composable
fun CyberButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = PrimaryColor, enabled: Boolean = true) {
    PremiumButton(text, onClick, modifier, color, enabled)
}

@Composable
fun NeonBadge(text: String, accentColor: Color, modifier: Modifier = Modifier) {
    PremiumBadge(text, accentColor, modifier)
}

@Composable
fun CyberBadge(text: String, modifier: Modifier = Modifier, accentColor: Color = PrimaryColor) {
    PremiumBadge(text, accentColor, modifier)
}
