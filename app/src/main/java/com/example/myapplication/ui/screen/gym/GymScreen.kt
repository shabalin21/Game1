package com.example.myapplication.ui.screen.gym

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.gym.model.*
import com.example.myapplication.ui.component.CyberButton
import com.example.myapplication.ui.component.GlassCard
import com.example.myapplication.ui.component.PetSprite
import com.example.myapplication.ui.theme.*

import androidx.compose.ui.graphics.graphicsLayer
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*

@Composable
fun GymScreen(
    viewModel: GymViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val pet by viewModel.petState.collectAsState()
    var selectedCategory by remember { mutableStateOf(TrainingCategory.STRENGTH) }
    var trainingExercise by remember { mutableStateOf<Exercise?>(null) }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CyberBackground(accentColor = PremiumGreen)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            ScreenHeader(
                title = "FITNESS_CENTER",
                subtitle = "NEURAL_PHYSICAL_OPTIMIZATION",
                accentColor = PremiumGreen,
                onBack = onBack
            )

            // Mini Buddy Preview
            pet?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), 
                    contentAlignment = Alignment.Center
                ) {
                    PetSprite(
                        emotion = it.emotionState,
                        psychology = it.psychology,
                        isSleeping = it.isSleeping,
                        equippedItems = it.equippedItems,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrainingCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = category },
                        color = if (isSelected) PremiumGreen.copy(alpha = 0.2f) else SurfaceDark,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, PremiumGreen) else null
                    ) {
                        Text(
                            text = category.name.replace("_", " & "),
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) PremiumGreen else Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exercise List
            GymData.exercises.filter { it.category == selectedCategory }.forEach { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onClick = { 
                        trainingExercise = exercise
                        viewModel.startTraining(exercise)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Training Animation Overlay
        trainingExercise?.let { exercise ->
            TrainingOverlay(exercise) {
                trainingExercise = null
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        accentColor = PremiumGreen
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PremiumGreen.copy(alpha = 0.1f))
                    .border(1.dp, PremiumGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(exercise.icon, fontSize = 20.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(exercise.description, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
            }
            
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = PremiumGreen.copy(alpha = 0.5f), modifier = Modifier.size(16.dp).graphicsLayer { rotationZ = 180f })
        }
    }
}

@Composable
fun TrainingOverlay(exercise: Exercise, onComplete: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(3000, easing = LinearEasing)
        ) { value, _ ->
            progress = value
        }
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(exercise.icon, fontSize = 72.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "OPTIMIZING_${exercise.name.uppercase()}",
                style = MaterialTheme.typography.displaySmall,
                color = PremiumGreen,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(240.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PremiumGreen,
                trackColor = Color.White.copy(alpha = 0.1f),
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AmbientGymBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(PremiumBlue.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
        )
    }
}

