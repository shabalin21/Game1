package com.example.myapplication.ui.screen.buddy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.AppearanceRegistry
import com.example.myapplication.domain.model.BuddyAppearance
import com.example.myapplication.ui.theme.*

@Composable
fun BuddyAppearanceEditor(
    appearance: BuddyAppearance,
    onUpdateAppearance: (BuddyAppearance) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(SurfaceDark.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("CHARACTER CUSTOMIZATION", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(16.dp))

        // HAIRSTYLE
        AppearanceCategory("HAIRSTYLE") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AppearanceRegistry.hairstyles) { hair ->
                    HairstyleCard(
                        name = hair,
                        isSelected = appearance.hairstyle == hair,
                        onClick = { onUpdateAppearance(appearance.copy(hairstyle = hair)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SKIN TONE
        AppearanceCategory("SKIN TONE") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(AppearanceRegistry.skinTones) { color ->
                    ColorCircle(
                        color = try { Color(android.graphics.Color.parseColor(color)) } catch (e: Exception) { Color.Gray },
                        isSelected = appearance.skinTone == color,
                        onClick = { onUpdateAppearance(appearance.copy(skinTone = color)) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // EYES
        AppearanceCategory("EYE TYPE") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AppearanceRegistry.eyeTypes) { eye ->
                    HairstyleCard( // Reusing HairstyleCard for simplicity
                        name = eye,
                        isSelected = appearance.eyeType == eye,
                        onClick = { onUpdateAppearance(appearance.copy(eyeType = eye)) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppearanceCategory(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun HairstyleCard(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp, 40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CyberPink.copy(alpha = 0.2f) else SurfaceDark)
            .border(1.dp, if (isSelected) CyberPink else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(name.uppercase(), style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Black)
    }
}

@Composable
fun ColorCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
            .clickable { onClick() }
    )
}
