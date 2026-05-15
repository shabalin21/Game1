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
    private var musicPlayer: MediaPlayer? = null
    private var ambientPlayer: MediaPlayer? = null
    private val layers = mutableMapOf<String, MediaPlayer>()

    fun setLayer(id: String, resId: Int, volume: Float) {
        val player = layers[id]
        if (player == null) {
            try {
                val mp = MediaPlayer.create(context, resId).apply {
                    isLooping = true
                    setVolume(volume, volume)
                    start()
                }
                layers[id] = mp
            } catch (e: Exception) {
                Timber.e(e, "Failed to start audio layer: $id")
            }
        } else {
            player.setVolume(volume, volume)
        }
    }

    fun stopLayer(id: String) {
        layers[id]?.let {
            it.stop()
            it.release()
        }
        layers.remove(id)
    }

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
