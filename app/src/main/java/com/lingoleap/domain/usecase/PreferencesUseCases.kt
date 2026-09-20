package com.lingoleap.domain.usecase

import com.lingoleap.domain.model.UserPreferences
import com.lingoleap.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserPreferencesUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    operator fun invoke(): Flow<UserPreferences> = repository.observePreferences()
}
//class CompleteOnboardingUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
//    suspend operator fun invoke() = repository.completeOnboarding()
//}
//class SaveLanguagePairUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
//    suspend operator fun invoke(sourceId: String, targetId: String, pairId: String) = repository.saveLanguagePair(sourceId, targetId, pairId)
//}
class ClearUserPreferencesUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke() = repository.clear()
}
