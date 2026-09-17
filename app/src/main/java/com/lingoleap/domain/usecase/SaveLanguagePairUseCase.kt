package com.lingoleap.domain.usecase

import com.lingoleap.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveLanguagePairUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(
        sourceLanguageId: String,
        targetLanguageId: String,
        pairId: String
    ) {
        repository.saveLanguagePair(
            sourceLanguageId = sourceLanguageId,
            targetLanguageId = targetLanguageId,
            pairId = pairId
        )
    }
}