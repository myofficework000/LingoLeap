package com.lingoleap.domain.usecase

import com.lingoleap.domain.achievement.AchievementRules
import com.lingoleap.domain.model.Achievement
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.repository.LearningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLearnerProgressUseCase @Inject constructor(private val repository: LearningRepository) {
    operator fun invoke(): Flow<LearnerProgress> = repository.observeProgress()
}
class CompleteLessonUseCase @Inject constructor(private val repository: LearningRepository) {
    suspend operator fun invoke(lessonId: String): LearnerProgress = repository.completeLesson(lessonId)
}
class GetAchievementsUseCase @Inject constructor(private val repository: LearningRepository) {
    suspend operator fun invoke(): List<Achievement> = AchievementRules.evaluate(repository.getProgress())
    fun observe(): Flow<List<Achievement>> = repository.observeProgress().map(AchievementRules::evaluate)
}
