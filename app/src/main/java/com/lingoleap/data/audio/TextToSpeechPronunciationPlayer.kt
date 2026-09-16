package com.lingoleap.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import com.lingoleap.domain.audio.PronunciationPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.util.Locale

@Singleton
class TextToSpeechPronunciationPlayer @Inject constructor(
    @param:ApplicationContext private val context: Context
) : PronunciationPlayer {

    private var engine: TextToSpeech? = null

    override suspend fun play(text: String, languageTag: String): Boolean {
        val tts = engine ?: initialize().also { engine = it }
        val locale = Locale.forLanguageTag(languageTag)
        val languageResult = tts.setLanguage(locale)
        if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            return false
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "lingoleap-$languageTag")
        return true
    }

    private suspend fun initialize(): TextToSpeech =
        suspendCancellableCoroutine { continuation ->
            var created: TextToSpeech? = null
            created = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    continuation.resume(created!!)
                } else {
                    continuation.resumeWithException(IllegalStateException("Text-to-speech is unavailable"))
                }
            }
            continuation.invokeOnCancellation {
                created.shutdown()
            }
        }

    override fun release() {
        engine?.stop()
        engine?.shutdown()
        engine = null
    }
}