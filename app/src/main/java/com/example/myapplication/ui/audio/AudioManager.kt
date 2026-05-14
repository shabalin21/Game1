package com.example.myapplication.ui.audio

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Placeholder for actual resource IDs
    // In a real production app, these would be R.raw.feed, R.raw.pet, etc.
    
    private var musicPlayer: MediaPlayer? = null
    private var ambientPlayer: MediaPlayer? = null

    fun playSfx(resId: Int) {
        try {
            val mp = MediaPlayer.create(context, resId)
            mp.setOnCompletionListener { it.release() }
            mp.start()
        } catch (e: Exception) {
            Timber.e(e, "Failed to play SFX: $resId")
        }
    }

    fun startAmbient(resId: Int) {
        if (ambientPlayer?.isPlaying == true) return
        
        ambientPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            setVolume(0.3f, 0.3f)
            start()
        }
    }

    fun stopAmbient() {
        ambientPlayer?.stop()
        ambientPlayer?.release()
        ambientPlayer = null
    }

    fun startMusic(resId: Int) {
        if (musicPlayer?.isPlaying == true) return
        
        musicPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            start()
        }
    }

    fun stopMusic() {
        musicPlayer?.stop()
        musicPlayer?.release()
        musicPlayer = null
    }
}
