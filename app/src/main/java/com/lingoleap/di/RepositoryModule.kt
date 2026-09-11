package com.lingoleap.di

import com.lingoleap.data.repository.LearningRepositoryImpl
import com.lingoleap.domain.repository.LearningRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindLearningRepository(implementation: LearningRepositoryImpl): LearningRepository
}
