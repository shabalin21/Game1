package com.example.myapplication.domain.social

import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.model.SocialPost
import com.example.myapplication.domain.model.WorldState
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.WorldEventType
import com.example.myapplication.domain.event.WorldEventImpact
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID
import kotlin.random.Random

@Singleton
class SocialManager @Inject constructor(
    private val petRepository: PetRepository
) {
    private data class NpcBuddy(val name: String, val icon: String)

    private val npcs = listOf(
        NpcBuddy("CyberCat", "🐱"),
        NpcBuddy("RoboDog", "🐶"),
        NpcBuddy("NeonBird", "🐦"),
        NpcBuddy("GlitchRat", "🐭"),
        NpcBuddy("VoidFish", "🐟")
    )

    suspend fun generateNpcPost(event: GameplayEvent.WorldEventTriggered?) {
        val pet = petRepository.getPetState().first() ?: return
        val npc = npcs.random()
        
        val content = if (event != null) {
            when (event.type) {
                WorldEventType.CRYPTO -> {
                    if (event.impact == WorldEventImpact.POSITIVE) {
                        "To the moon! 🚀 Just made a fortune on ${event.title}."
                    } else {
                        "Ouch. ${event.title} hit my wallet hard today. #HODL"
                    }
                }
                WorldEventType.FINANCIAL -> "Financial shift in the sector. Time to rebalance."
                WorldEventType.GAMBLING -> "Happy hour at the slots! See you there? 🎰"
                else -> "Another day in Metropolis. #Routine"
            }
        } else {
            listOf(
                "Just finished my morning routine. Feeling optimal. ${npc.icon}",
                "Anyone else seeing those weird atmospheric shifts? #Metropolis",
                "Met a cool digital lifeform today. The ecosystem is growing.",
                "Thinking about upgrading my core. Suggestions?",
                "Metropolis looks beautiful at this hour. #Atmosphere"
            ).random()
        }

        val newPost = SocialPost(
            id = "npc_${UUID.randomUUID()}",
            content = "[${npc.name}] $content",
            likes = Random.nextInt(20, 300)
        )

        val updatedSocial = pet.social.copy(
            posts = (listOf(newPost) + pet.social.posts).take(30)
        )
        
        petRepository.savePetState(pet.copy(social = updatedSocial))
    }

    suspend fun getTrendingPost(): String {
        val pet = petRepository.getPetState().first() ?: return "Connected to Metropolis Grid."
        return pet.social.posts.firstOrNull()?.content ?: "Ecosystem state: STABLE."
    }

    suspend fun flexPossession(itemId: String, itemName: String) {
        val pet = petRepository.getPetState().first() ?: return
        
        val newFollowers = Random.nextInt(10, 100) * (pet.level / 2).coerceAtLeast(1)
        val newPost = SocialPost(
            id = UUID.randomUUID().toString(),
            content = "Just got my new $itemName! #LivingLarge #CyberLife",
            likes = Random.nextInt(50, 500)
        )
        
        val updatedSocial = pet.social.copy(
            followers = pet.social.followers + newFollowers,
            posts = (listOf(newPost) + pet.social.posts).take(20)
        )
        
        petRepository.savePetState(pet.copy(social = updatedSocial))
        Timber.i("Social: Gained $newFollowers followers flexing $itemName")
    }
}
