package com.example.myapplication.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.EmotionState
import com.example.myapplication.domain.model.Mood
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.animation.core.rememberCreatureAnimationController
import com.example.myapplication.ui.theme.*

@Composable
fun PetSprite(
    emotion: EmotionState,
    psychology: com.example.myapplication.domain.model.PsychologyState,
    isSleeping: Boolean,
    appearance: com.example.myapplication.domain.model.BuddyAppearance = com.example.myapplication.domain.model.BuddyAppearance(),
    employment: com.example.myapplication.domain.model.EmploymentState = com.example.myapplication.domain.model.EmploymentState(),
    onActivateDevLab: () -> Unit = {},
    onPet: () -> Unit = {},
    targetFps: Int = 60,
    modifiers: List<com.example.myapplication.domain.model.TimedModifier> = emptyList(),
    equippedItems: Map<com.example.myapplication.domain.model.ItemCategory, String> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val animController = rememberCreatureAnimationController()
    
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    // Update the controller in a frame loop
    LaunchedEffect(emotion, psychology, isSleeping, targetFps) {
        var lastTime = withFrameMillis { it }
        val frameTimeTarget = 1000L / targetFps
        
        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = (currentTime - lastTime) / 1000f
                lastTime = currentTime
                animController.update(deltaTime, emotion, psychology, isSleeping)
            }
            if (targetFps < 120) {
                kotlinx.coroutines.delay(frameTimeTarget)
            }
        }
    }

    val baseBodyColor = try { Color(android.graphics.Color.parseColor(appearance.skinTone)) } catch (e: Exception) { PrimaryColor }
    
    val bodyColorState = animateColorAsState(
        targetValue = when {
            isSleeping -> Color(0xFF3F51B5)
            emotion.primaryMood == Mood.HAPPY -> baseBodyColor.copy(alpha = 0.8f) // Slight tint for mood
            emotion.primaryMood == Mood.EXCITED -> baseBodyColor.copy(alpha = 0.9f)
            emotion.primaryMood == Mood.ANGRY -> NeonOrange
            emotion.primaryMood == Mood.SAD -> Color(0xFF546E7A)
            else -> baseBodyColor
        },
        label = "BodyColor"
    )

    Box(
        modifier = modifier
            .size(240.dp)
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        animController.react()
                        onPet()

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTapTime < 500) {
                            tapCount++
                        } else {
                            tapCount = 1
                        }
                        lastTapTime = currentTime
                        if (tapCount >= 7) {
                            onActivateDevLab()
                            tapCount = 0
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Mood Aura - REWORKED: Soft Radial Glow
        Box(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            bodyColorState.value.copy(alpha = 0.3f * emotion.intensity),
                            Color.Transparent
                        )
                    )
                )
        )

        // BODY
        Box(
            modifier = Modifier
                .fillMaxSize(0.75f)
                .graphicsLayer {
                    translationY = animController.bodyOffsetY
                    translationX = animController.jitterX
                    scaleX = animController.bodyScaleX
                    scaleY = animController.bodyScaleY
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Ground Shadow - Refined
                drawOval(
                    color = Color.Black.copy(alpha = 0.15f),
                    topLeft = androidx.compose.ui.geometry.Offset(center.x - 30.dp.toPx(), size.height - 10.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(60.dp.toPx(), 12.dp.toPx())
                )

                // Body Shape
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            bodyColorState.value.copy(alpha = 0.9f),
                            bodyColorState.value
                        )
                    )
                )
                
                // Subtle Highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Clothing
                drawClothing(equippedItems, size)
            }

            // FACE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // EYES
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { translationX = animController.eyeOffsetX },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Eye(animController.eyeScaleY, emotion.primaryMood, appearance)
                    Eye(animController.eyeScaleY, emotion.primaryMood, appearance)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // MOUTH
                if (!isSleeping) {
                    Mouth(emotion.primaryMood)
                }
            }
        }

        // Reaction Overlays (Emoji) - REWORKED: Alpha 0.5f
        if (emotion.intensity > 0.7f && !isSleeping) {
            Text(
                text = when (emotion.primaryMood) {
                    Mood.HAPPY -> "✨"
                    Mood.EXCITED -> "💖"
                    Mood.ANGRY -> "💢"
                    Mood.SAD -> "💧"
                    Mood.BORED -> "💤"
                    else -> ""
                },
                fontSize = 40.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 20.dp)
                    .graphicsLayer { alpha = 0.5f }
            )
        }
    }
}

private fun DrawScope.drawClothing(equipped: Map<com.example.myapplication.domain.model.ItemCategory, String>, size: androidx.compose.ui.geometry.Size) {
    // HEAD SLOT
    if (equipped.containsKey(com.example.myapplication.domain.model.ItemCategory.HEAD)) {
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = 30.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height * 0.1f)
        )
    }

    // TOP SLOT
    if (equipped.containsKey(com.example.myapplication.domain.model.ItemCategory.TOP)) {
        drawRect(
            color = Color.White.copy(alpha = 0.15f),
            topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * 0.35f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.5f, size.height * 0.4f)
        )
    }
}

@Composable
private fun Eye(
    scaleY: Float, 
    mood: Mood,
    appearance: com.example.myapplication.domain.model.BuddyAppearance = com.example.myapplication.domain.model.BuddyAppearance()
) {
    val eyeColor = if (mood == Mood.ANGRY) NeonPink else try { 
        Color(android.graphics.Color.parseColor(appearance.eyeColor)) 
    } catch (e: Exception) { Color.White }

    Box(
        modifier = Modifier
            .size(28.dp)
            .graphicsLayer { this.scaleY = scaleY }
            .background(eyeColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(Color(0xFF0D1117), CircleShape)
        )
    }
}

@Composable
private fun Mouth(mood: Mood) {
    val mouthWidth = when(mood) {
        Mood.HAPPY, Mood.EXCITED -> 30.dp
        Mood.ANGRY -> 24.dp
        else -> 16.dp
    }
    val mouthHeight = when(mood) {
        Mood.HAPPY, Mood.EXCITED -> 12.dp
        Mood.SAD, Mood.ANGRY -> 4.dp
        else -> 4.dp
    }
    
    Box(
        modifier = Modifier
            .size(mouthWidth, mouthHeight)
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun PetSpritePreview() {
    PetSprite(
        emotion = EmotionState(primaryMood = Mood.HAPPY, intensity = 0.8f),
        psychology = com.example.myapplication.domain.model.PsychologyState(),
        isSleeping = false,
        appearance = com.example.myapplication.domain.model.BuddyAppearance(),
        equippedItems = emptyMap(),
        modifier = Modifier.size(200.dp)
    )
}
