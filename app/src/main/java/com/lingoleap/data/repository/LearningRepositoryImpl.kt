package com.lingoleap.data.repository

import com.lingoleap.data.local.LearningCatalogDataSource
import com.lingoleap.data.local.DailyChallengeDataSource
import com.lingoleap.data.local.AchievementCatalogDataSource
import com.lingoleap.data.local.progress.ProgressLocalDataSource
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.DailyChallenge
import com.lingoleap.domain.model.AchievementDefinition
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.Quiz
import com.lingoleap.domain.repository.LearningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class LearningRepositoryImpl @Inject constructor(
    private val source: LearningCatalogDataSource,
    private val dailyChallengeSource: DailyChallengeDataSource,
    private val achievementCatalogSource: AchievementCatalogDataSource,
    private val progressSource: ProgressLocalDataSource,
) : LearningRepository {
    override suspend fun getLanguages(): List<Language> = source.languages()
    override suspend fun getLanguagePairs(): List<LanguagePair> = source.languagePairs()
    override suspend fun getCourses(): List<Course> = source.courses()
    override suspend fun getQuizzes(lessonId: String): List<Quiz> =
        source.quizzes(lessonId)
    override suspend fun getDailyChallenges(): List<DailyChallenge> {
        val progress = getProgress()
        val activePairId = progress.activeCourseId.removePrefix("course-")
        return dailyChallengeSource.challenges().filter { challenge ->
            challenge.lessonId.startsWith("$activePairId-") &&
                challenge.id !in progress.completedDailyChallengeIds
        }
    }
    override suspend fun getAchievementDefinitions(): List<AchievementDefinition> =
        achievementCatalogSource.definitions()
    override suspend fun setActiveLanguagePair(languagePairId: String): LearnerProgress {
        val course = source.courses().firstOrNull { it.languagePairId == languagePairId }
            ?: return getProgress()
        val updated = getProgress().copy(activeCourseId = course.id)
        progressSource.save(updated)
        return updated
    }
    override suspend fun completeDailyChallenge(challengeId: String): LearnerProgress {
        val challenge = dailyChallengeSource.challenges().firstOrNull { it.id == challengeId }
            ?: return getProgress()
        val current = getProgress()
        if (challengeId in current.completedDailyChallengeIds) return current
        val updated = current.copy(
            completedDailyChallengeIds = current.completedDailyChallengeIds + challengeId,
            xp = current.xp + challenge.xpReward,
        )
        progressSource.save(updated)
        return updated
    }
    override suspend fun getProgress(): LearnerProgress = progressSource.get() ?: source.progress().let { defaultProgress ->
        progressSource.save(defaultProgress)
        defaultProgress
    }
    override fun observeProgress(): Flow<LearnerProgress> = progressSource.observe().onStart { getProgress() }
    override suspend fun completeLesson(lessonId: String): LearnerProgress {
        val current = getProgress()
        return current.copy(
            completedLessonIds = current.completedLessonIds + lessonId,
            xp = current.xp + if (lessonId in current.completedLessonIds) 0 else 10,
        ).let { updated ->
            progressSource.save(updated)
            updated
        }
    }
}
