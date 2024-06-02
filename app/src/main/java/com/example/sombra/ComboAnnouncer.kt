package com.example.sombra

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class ComboAnnouncer(private val context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)
    private var speaking: Boolean = false

    init {
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                speaking = true
            }

            override fun onDone(utteranceId: String?) {
                // Speech synthesis completed
                speaking = false
            }

            override fun onError(utteranceId: String?) {
                // Error occurred during speech synthesis
                TODO("Hmm")
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US)
        } else {
            // TextToSpeech initialization failed
            // Handle initialization failure
        }
    }

    fun speak(text: String) {
        if (speaking) {
            return
        }

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "unique_id")
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, "unique_id")
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
