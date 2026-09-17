package com.lingoleap.domain.usecase

import com.lingoleap.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveOnboardingStatusUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.observePreferences()
            .map { it.onboardingCompleted }
    }
}