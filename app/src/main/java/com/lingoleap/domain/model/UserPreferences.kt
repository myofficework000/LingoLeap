package com.lingoleap.domain.model


data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val sourceLanguageId: String? = null,
    val targetLanguageId: String? = null,
    val activeLanguagePairId: String? = null
)