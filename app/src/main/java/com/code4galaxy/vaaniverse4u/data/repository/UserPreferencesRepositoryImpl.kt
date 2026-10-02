package com.code4galaxy.vaaniverse4u.data.repository

import com.code4galaxy.vaaniverse4u.data.local.preferences.PreferencesDataSource
import com.code4galaxy.vaaniverse4u.domain.model.UserPreferences
import com.code4galaxy.vaaniverse4u.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(private val source: PreferencesDataSource) : UserPreferencesRepository {
    override fun observePreferences(): Flow<UserPreferences> = source.preferences
    override suspend fun completeOnboarding() { source.completeOnboarding() }
    override suspend fun saveLanguagePair(sourceLanguageId: String, targetLanguageId: String, pairId: String) =
        source.saveLanguagePair(sourceLanguageId, targetLanguageId, pairId)
    override suspend fun saveDailyGoal(lessons: Int) = source.saveDailyGoal(lessons)
    override suspend fun saveAccessibilitySettings(textScale: Float, highContrastEnabled: Boolean) =
        source.saveAccessibilitySettings(textScale, highContrastEnabled)
    override suspend fun clear() { source.clear() }
}
