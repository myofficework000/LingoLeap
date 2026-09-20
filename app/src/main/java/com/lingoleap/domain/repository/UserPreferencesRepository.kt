package com.lingoleap.domain.repository

import com.lingoleap.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun completeOnboarding(bool: Boolean)
    suspend fun saveLanguagePair(sourceLanguageId: String, targetLanguageId: String, pairId: String)
    suspend fun clear()
}
