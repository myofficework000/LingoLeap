package com.lingoleap.domain.repository

import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.Quiz
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    suspend fun getLanguages(): List<Language>
    suspend fun getLanguagePairs(): List<LanguagePair>
    suspend fun getCourses(): List<Course>
    suspend fun getQuiz(lessonId: String): Quiz?
    suspend fun getProgress(): LearnerProgress
    fun observeProgress(): Flow<LearnerProgress>
    suspend fun completeLesson(lessonId: String): LearnerProgress
}
