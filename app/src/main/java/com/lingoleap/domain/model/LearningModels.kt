package com.lingoleap.domain.model

data class Language(
    val id: String,
    val name: String,
    val nativeName: String,
    val script: String,
    val locale: String
)

data class LanguagePair(val id: String, val sourceLanguageId: String, val targetLanguageId: String)
data class Course(
    val id: String,
    val languagePairId: String,
    val title: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val title: String,
    val order: Int,
    val words: List<VocabularyWord>,
    val pronunciationLanguageTag: String
)

data class VocabularyWord(
    val id: String,
    val sourceText: String,
    val targetText: String,
    val transliteration: String? = null
)

data class Quiz(
    val id: String,
    val lessonId: String,
    val prompt: String,
    val choices: List<String>,
    val correctAnswer: String
)

data class DailyChallenge(
    val id: String,
    val lessonId: String,
    val title: String,
    val prompt: String,
    val choices: List<String>,
    val correctAnswer: String,
    val hint: String,
    val xpReward: Int,
)

data class LearnerProgress(
    val activeCourseId: String,
    val completedLessonIds: Set<String>,
    val streakDays: Int,
    val xp: Int
)

data class UserPreferences(
    val hasCompletedOnboarding: Boolean = false,
    val sourceLanguageId: String? = null,
    val targetLanguageId: String? = null,
    val activeLanguagePairId: String? = null,
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
)

enum class LearningPathNodeState { COMPLETED, CURRENT, LOCKED }
data class LearningPathNode(val lesson: Lesson, val state: LearningPathNodeState)
