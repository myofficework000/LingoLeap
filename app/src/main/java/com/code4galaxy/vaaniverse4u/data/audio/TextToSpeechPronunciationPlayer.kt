package com.code4galaxy.vaaniverse4u.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import com.code4galaxy.vaaniverse4u.domain.audio.PronunciationPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val initializationMutex = Mutex()

    override suspend fun play(text: String, languageTag: String): Boolean {
        // Lesson and review actions can arrive together on a fast tap. Initialise only once
        // rather than creating competing platform engines.
        val tts = engine ?: initializationMutex.withLock {
            engine ?: initialize().also { engine = it }
        }
        val locale = Locale.forLanguageTag(languageTag)
        val languageResult = tts.setLanguage(locale)
        if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            return false
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vaaniverse4u-$languageTag")
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
