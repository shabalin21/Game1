package com.example.myapplication.ui.screen.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.SocialPost
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*

@Composable
fun SocialFeedScreen(
    viewModel: SocialViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val pet by viewModel.petState.collectAsState()
    val social = pet?.social ?: com.example.myapplication.domain.model.SocialState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CyberBackground(accentColor = NeonCyan)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            ScreenHeader(
                title = "NEURAL_LINK",
                subtitle = "SOCIAL_FEED",
                accentColor = NeonCyan,
                onBack = onBack
            )

            // Stats Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatBox(label = "FOLLOWERS", value = social.followers.toString(), modifier = Modifier.weight(1f))
                StatBox(label = "PRESTIGE", value = social.prestige.toString(), modifier = Modifier.weight(1f))
            }

            Text(
                text = "RECENT POSTS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(social.posts) { post ->
                    SocialPostCard(post)
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    CyberCard(accentColor = NeonCyan, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
            Text(value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun SocialPostCard(post: SocialPost) {
    CyberCard(accentColor = Color.White.copy(alpha = 0.1f)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).background(NeonCyan, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("BUDDY", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("❤️ ${post.likes}", style = MaterialTheme.typography.labelSmall, color = NeonPink)
            }
        }
    }
}
