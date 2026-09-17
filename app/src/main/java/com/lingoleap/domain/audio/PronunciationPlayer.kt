package com.lingoleap.domain.audio

/** Platform-neutral audio boundary. UI and ViewModels never call Android TTS directly. */
interface PronunciationPlayer {
    suspend fun play(text: String, languageTag: String): Boolean
    fun release()
}
