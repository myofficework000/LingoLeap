package com.code4galaxy.vaaniverse4u.di

import com.code4galaxy.vaaniverse4u.data.audio.TextToSpeechPronunciationPlayer
import com.code4galaxy.vaaniverse4u.domain.audio.PronunciationPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {
    @Binds abstract fun bindPronunciationPlayer(implementation: TextToSpeechPronunciationPlayer): PronunciationPlayer
}
