package com.lingoleap.domain.usecase

import com.lingoleap.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted(true)
    }
}