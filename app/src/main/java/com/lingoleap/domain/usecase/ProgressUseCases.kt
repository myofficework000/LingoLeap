package com.lingoleap.domain.usecase

import com.lingoleap.domain.achievement.AchievementRules
import com.lingoleap.domain.model.Achievement
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.repository.LearningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLearnerProgressUseCase @Inject constructor(private val repository: LearningRepository) {
    operator fun invoke(): Flow<LearnerProgress> = repository.observeProgress()
}
class CompleteLessonUseCase @Inject constructor(private val repository: LearningRepository) {
    suspend operator fun invoke(lessonId: String): LearnerProgress = repository.completeLesson(lessonId)
}
class CompleteDailyChallengeUseCase @Inject constructor(private val repository: LearningRepository) {
    suspend operator fun invoke(challengeId: String): LearnerProgress = repository.completeDailyChallenge(challengeId)
}
class GetAchievementsUseCase @Inject constructor(private val repository: LearningRepository) {
    suspend operator fun invoke(): List<Achievement> = AchievementRules.evaluate(
        progress = repository.getProgress(),
        definitions = repository.getAchievementDefinitions(),
    )
    fun observe(): Flow<List<Achievement>> = flow {
        val definitions = repository.getAchievementDefinitions()
        emitAll(repository.observeProgress().map { progress -> AchievementRules.evaluate(progress, definitions) })
    }
}
