package com.lingoleap.domain.usecase

import com.lingoleap.domain.model.UserPreferences
import com.lingoleap.domain.repository.UserPreferencesRepository
import com.lingoleap.domain.repository.LearningRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserPreferencesUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    operator fun invoke(): Flow<UserPreferences> = repository.observePreferences()
}
class CompleteOnboardingUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke() = repository.completeOnboarding()
}
class SaveLanguagePairUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(sourceId: String, targetId: String, pairId: String) = repository.saveLanguagePair(sourceId, targetId, pairId)
}
class SetActiveLanguagePairUseCase @Inject constructor(private val repository: LearningRepository) {
    suspend operator fun invoke(languagePairId: String) = repository.setActiveLanguagePair(languagePairId)
}
class SaveDailyGoalUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(lessons: Int) = repository.saveDailyGoal(lessons)
}
class ClearUserPreferencesUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke() = repository.clear()
}
