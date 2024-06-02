package com.example.sombra

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var bellSoundId: Int = 0

    init {
        // Initialize SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load boxing bell sound
        bellSoundId = soundPool?.load(context, R.raw.round_boxing_bell, 1) ?: 0
    }

    fun playBoxingBell() {
        // Play boxing bell sound
        soundPool?.play(bellSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun release() {
        // Release resources
        soundPool?.release()
        soundPool = null
    }
}
