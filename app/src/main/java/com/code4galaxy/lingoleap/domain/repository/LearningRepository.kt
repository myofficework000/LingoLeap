package com.code4galaxy.lingoleap.domain.repository

import com.code4galaxy.lingoleap.domain.model.Course
import com.code4galaxy.lingoleap.domain.model.DailyChallenge
import com.code4galaxy.lingoleap.domain.model.AchievementDefinition
import com.code4galaxy.lingoleap.domain.model.Language
import com.code4galaxy.lingoleap.domain.model.LanguagePair
import com.code4galaxy.lingoleap.domain.model.LearnerProgress
import com.code4galaxy.lingoleap.domain.model.Quiz
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    suspend fun getLanguages(): List<Language>
    suspend fun getLanguagePairs(): List<LanguagePair>
    suspend fun getCourses(): List<Course>
    suspend fun getQuizzes(lessonId: String): List<Quiz>
    suspend fun getDailyChallenges(): List<DailyChallenge>
    suspend fun getAchievementDefinitions(): List<AchievementDefinition>
    suspend fun setActiveLanguagePair(languagePairId: String): LearnerProgress
    suspend fun completeDailyChallenge(challengeId: String): LearnerProgress
    suspend fun getProgress(): LearnerProgress
    fun observeProgress(): Flow<LearnerProgress>
    suspend fun completeLesson(lessonId: String): LearnerProgress
    suspend fun addReviewWord(wordId: String): LearnerProgress
    suspend fun removeReviewWord(wordId: String): LearnerProgress
    suspend fun resetProgress(): LearnerProgress
}
