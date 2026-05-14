package com.example.myapplication.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState

/**
 * Utility for the "Squash & Stretch" breathing effect.
 */
@Composable
fun rememberBreathingScale(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    return infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
}

@Composable
fun rememberBlinkingAlpha(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "blinking")
    return infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                1.0f at 0
                1.0f at 2800
                0.0f at 2900
                1.0f at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )
}
