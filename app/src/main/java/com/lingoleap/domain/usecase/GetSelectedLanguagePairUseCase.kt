package com.lingoleap.domain.usecase

import com.lingoleap.domain.model.UserPreferences
import com.lingoleap.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelectedLanguagePairUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> {
        return repository.observePreferences()
    }
}