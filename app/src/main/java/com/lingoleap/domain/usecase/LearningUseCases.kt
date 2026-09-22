package com.lingoleap.domain.usecase

import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.DailyChallenge
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.Quiz
import com.lingoleap.domain.repository.LearningRepository
import javax.inject.Inject

class GetSupportedLanguagesUseCase @Inject constructor(private val repository: LearningRepository) { suspend operator fun invoke(): List<Language> = repository.getLanguages() }
class GetLanguagePairsUseCase @Inject constructor(private val repository: LearningRepository) { suspend operator fun invoke(): List<LanguagePair> = repository.getLanguagePairs() }
class GetCoursesUseCase @Inject constructor(private val repository: LearningRepository) { suspend operator fun invoke(): List<Course> = repository.getCourses() }
class GetLessonQuizUseCase @Inject constructor(
    private val repository: LearningRepository
) {
    suspend operator fun invoke(
        lessonId: String
    ): List<Quiz> =
        repository.getQuizzes(lessonId)
}
class GetDailyChallengesUseCase @Inject constructor(private val repository: LearningRepository) { suspend operator fun invoke(): List<DailyChallenge> = repository.getDailyChallenges() }
class GetLearnerProgressUseCase @Inject constructor(private val repository: LearningRepository) { suspend operator fun invoke(): LearnerProgress = repository.getProgress() }
