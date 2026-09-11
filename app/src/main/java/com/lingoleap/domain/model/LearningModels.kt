package com.lingoleap.domain.model

data class Language(val id: String, val name: String, val nativeName: String, val script: String)
data class LanguagePair(val id: String, val sourceLanguageId: String, val targetLanguageId: String)
data class Course(val id: String, val languagePairId: String, val title: String, val lessons: List<Lesson>)
data class Lesson(val id: String, val title: String, val order: Int, val words: List<VocabularyWord>)
data class VocabularyWord(val id: String, val sourceText: String, val targetText: String, val transliteration: String? = null)
data class Quiz(val id: String, val lessonId: String, val prompt: String, val choices: List<String>, val correctAnswer: String)
data class LearnerProgress(val activeCourseId: String, val completedLessonIds: Set<String>, val streakDays: Int, val xp: Int)
