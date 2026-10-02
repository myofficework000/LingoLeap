package com.code4galaxy.vaaniverse4u.domain.repository

import com.code4galaxy.vaaniverse4u.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun completeOnboarding()
    suspend fun saveLanguagePair(sourceLanguageId: String, targetLanguageId: String, pairId: String)
    suspend fun saveDailyGoal(lessons: Int)
    suspend fun saveAccessibilitySettings(textScale: Float, highContrastEnabled: Boolean)
    suspend fun clear()
}
