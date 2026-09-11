package com.lingoleap.data.repository

import com.lingoleap.data.local.LearningCatalogDataSource
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.Quiz
import com.lingoleap.domain.repository.LearningRepository
import javax.inject.Inject

class LearningRepositoryImpl @Inject constructor(private val source: LearningCatalogDataSource) : LearningRepository {
    override suspend fun getLanguages(): List<Language> = source.languages()
    override suspend fun getLanguagePairs(): List<LanguagePair> = source.languagePairs()
    override suspend fun getCourses(): List<Course> = source.courses()
    override suspend fun getQuiz(lessonId: String): Quiz? = source.quiz(lessonId)
    override suspend fun getProgress(): LearnerProgress = source.progress()
}
