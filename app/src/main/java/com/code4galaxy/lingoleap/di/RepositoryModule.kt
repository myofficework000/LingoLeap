package com.code4galaxy.lingoleap.di

import com.code4galaxy.lingoleap.data.repository.LearningRepositoryImpl
import com.code4galaxy.lingoleap.data.repository.UserPreferencesRepositoryImpl
import com.code4galaxy.lingoleap.domain.repository.LearningRepository
import com.code4galaxy.lingoleap.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindLearningRepository(implementation: LearningRepositoryImpl): LearningRepository
    @Binds abstract fun bindUserPreferencesRepository(implementation: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
