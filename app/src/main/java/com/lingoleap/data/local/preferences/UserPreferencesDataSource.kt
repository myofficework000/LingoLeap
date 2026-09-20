package com.lingoleap.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lingoleap.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val ONBOARDING_COMPLETED =
            booleanPreferencesKey("onboarding_completed")

        val SOURCE_LANGUAGE_ID =
            stringPreferencesKey("source_language_id")

        val TARGET_LANGUAGE_ID =
            stringPreferencesKey("target_language_id")

        val ACTIVE_LANGUAGE_PAIR_ID =
            stringPreferencesKey("active_language_pair_id")
    }

    val preferences: Flow<UserPreferences> =
        context.dataStore.data.map { prefs ->

            UserPreferences(
                hasCompletedOnboarding =
                    prefs[Keys.ONBOARDING_COMPLETED] ?: false,

                sourceLanguageId =
                    prefs[Keys.SOURCE_LANGUAGE_ID],

                targetLanguageId =
                    prefs[Keys.TARGET_LANGUAGE_ID],

                activeLanguagePairId =
                    prefs[Keys.ACTIVE_LANGUAGE_PAIR_ID]
            )
        }

    suspend fun setOnboardingCompleted(
        completed: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveLanguagePair(
        sourceLanguageId: String,
        targetLanguageId: String,
        pairId: String
    ) {
        context.dataStore.edit { prefs ->

            prefs[Keys.SOURCE_LANGUAGE_ID] =
                sourceLanguageId

            prefs[Keys.TARGET_LANGUAGE_ID] =
                targetLanguageId

            prefs[Keys.ACTIVE_LANGUAGE_PAIR_ID] =
                pairId
        }
    }
}