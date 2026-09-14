package com.lingoleap.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import com.lingoleap.domain.audio.PronunciationPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class TextToSpeechPronunciationPlayer @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PronunciationPlayer {
    private var engine: TextToSpeech? = null

    override suspend fun play(text: String, languageTag: String) = withContext(Dispatchers.Main.immediate) {
        val textToSpeech = engine ?: initialize().also { engine = it }
        textToSpeech.language = Locale.forLanguageTag(languageTag)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "lingoleap_pronunciation")
        Unit
    }

    private suspend fun initialize(): TextToSpeech = suspendCancellableCoroutine { continuation ->
        var created: TextToSpeech? = null
        created = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) continuation.resume(created!!)
            else continuation.resumeWithException(IllegalStateException("Text-to-speech is unavailable"))
        }
        continuation.invokeOnCancellation { created.shutdown() }
    }

    override fun release() { engine?.shutdown(); engine = null }
}
