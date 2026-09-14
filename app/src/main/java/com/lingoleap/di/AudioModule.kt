package com.lingoleap.di

import com.lingoleap.data.audio.TextToSpeechPronunciationPlayer
import com.lingoleap.domain.audio.PronunciationPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {
    @Binds abstract fun bindPronunciationPlayer(implementation: TextToSpeechPronunciationPlayer): PronunciationPlayer
}
