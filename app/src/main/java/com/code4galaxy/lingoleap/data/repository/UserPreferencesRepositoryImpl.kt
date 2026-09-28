package com.code4galaxy.lingoleap.data.repository

import com.code4galaxy.lingoleap.data.local.preferences.PreferencesDataSource
import com.code4galaxy.lingoleap.domain.model.UserPreferences
import com.code4galaxy.lingoleap.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(private val source: PreferencesDataSource) : UserPreferencesRepository {
    override fun observePreferences(): Flow<UserPreferences> = source.preferences
    override suspend fun completeOnboarding() { source.completeOnboarding() }
    override suspend fun saveLanguagePair(sourceLanguageId: String, targetLanguageId: String, pairId: String) =
        source.saveLanguagePair(sourceLanguageId, targetLanguageId, pairId)
    override suspend fun saveDailyGoal(lessons: Int) = source.saveDailyGoal(lessons)
    override suspend fun clear() { source.clear() }
}
