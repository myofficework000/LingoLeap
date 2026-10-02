package com.code4galaxy.vaaniverse4u.domain.usecase

import com.code4galaxy.vaaniverse4u.domain.model.UserPreferences
import com.code4galaxy.vaaniverse4u.domain.repository.UserPreferencesRepository
import com.code4galaxy.vaaniverse4u.domain.repository.LearningRepository
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
class SaveAccessibilitySettingsUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(textScale: Float, highContrastEnabled: Boolean) =
        repository.saveAccessibilitySettings(textScale, highContrastEnabled)
}
class ClearUserPreferencesUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke() = repository.clear()
}
