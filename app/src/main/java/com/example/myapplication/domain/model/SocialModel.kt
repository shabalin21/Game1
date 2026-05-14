package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SocialState(
    val followers: Int = 0,
    val prestige: Int = 0,
    val popularity: Int = 0,
    val posts: List<SocialPost> = emptyList()
)

@Serializable
data class SocialPost(
    val id: String,
    val content: String,
    val likes: Int,
    val timestamp: Long = System.currentTimeMillis()
)
