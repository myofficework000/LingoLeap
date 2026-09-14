package com.lingoleap.data.repository

import com.lingoleap.data.local.LearningCatalogDataSource
import com.lingoleap.data.local.progress.ProgressLocalDataSource
import com.lingoleap.domain.model.Course
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
    private val progressSource: ProgressLocalDataSource,
) : LearningRepository {
    override suspend fun getLanguages(): List<Language> = source.languages()
    override suspend fun getLanguagePairs(): List<LanguagePair> = source.languagePairs()
    override suspend fun getCourses(): List<Course> = source.courses()
    override suspend fun getQuiz(lessonId: String): Quiz? = source.quiz(lessonId)
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
