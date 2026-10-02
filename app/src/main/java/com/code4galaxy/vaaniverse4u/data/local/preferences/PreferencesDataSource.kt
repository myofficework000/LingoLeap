package com.code4galaxy.vaaniverse4u.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.code4galaxy.vaaniverse4u.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesDataSource @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val preferences: Flow<UserPreferences> = dataStore.data.map { values ->
        UserPreferences(
            hasCompletedOnboarding = values[ONBOARDING_COMPLETE] ?: false,
            sourceLanguageId = values[SOURCE_LANGUAGE],
            targetLanguageId = values[TARGET_LANGUAGE],
            activeLanguagePairId = values[LANGUAGE_PAIR],
            dailyGoalLessons = values[DAILY_GOAL_LESSONS]?.toIntOrNull(),
            textScale = values[TEXT_SCALE] ?: 1f,
            highContrastEnabled = values[HIGH_CONTRAST] ?: false,
        )
    }

    suspend fun completeOnboarding() = dataStore.edit { it[ONBOARDING_COMPLETE] = true }

    suspend fun saveLanguagePair(sourceLanguageId: String, targetLanguageId: String, pairId: String) {
        dataStore.edit {
            it[SOURCE_LANGUAGE] = sourceLanguageId
            it[TARGET_LANGUAGE] = targetLanguageId
            it[LANGUAGE_PAIR] = pairId
        }
    }
    suspend fun saveDailyGoal(lessons: Int) {
        dataStore.edit { it[DAILY_GOAL_LESSONS] = lessons.toString() }
    }

    suspend fun saveAccessibilitySettings(textScale: Float, highContrastEnabled: Boolean) {
        dataStore.edit {
            it[TEXT_SCALE] = textScale.coerceIn(0.85f, 1.3f)
            it[HIGH_CONTRAST] = highContrastEnabled
        }
    }

    suspend fun clear() = dataStore.edit { it.clear() }

    private companion object {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val SOURCE_LANGUAGE = stringPreferencesKey("source_language")
        val TARGET_LANGUAGE = stringPreferencesKey("target_language")
        val LANGUAGE_PAIR = stringPreferencesKey("active_language_pair")
        val DAILY_GOAL_LESSONS = stringPreferencesKey("daily_goal_lessons")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    }
}
